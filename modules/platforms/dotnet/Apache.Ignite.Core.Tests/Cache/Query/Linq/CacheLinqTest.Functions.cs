/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// ReSharper disable SuspiciousTypeConversion.Global
// ReSharper disable MemberCanBePrivate.Global
// ReSharper disable AutoPropertyCanBeMadeGetOnly.Global
// ReSharper disable UnusedAutoPropertyAccessor.Global
// ReSharper disable StringIndexOfIsCultureSpecific.1
// ReSharper disable StringIndexOfIsCultureSpecific.2
// ReSharper disable StringCompareToIsCultureSpecific
// ReSharper disable StringCompareIsCultureSpecific.1
// ReSharper disable UnusedMemberInSuper.Global
namespace Apache.Ignite.Core.Tests.Cache.Query.Linq
{
    using System.Linq;
    using Apache.Ignite.Linq;
    using NUnit.Framework;

    /// <summary>
    /// Tests LINQ.
    /// </summary>
    public partial class CacheLinqTest
    {
        /// <summary>
        /// Tests where clause.
        /// </summary>
        [Test]
        public void TestWhere()
        {
            var cache = GetPersonCache().AsCacheQueryable();

            // Test const and var parameters
            const int age = 10;
            var key = 15;

            Assert.AreEqual(age, cache.Where(x => x.Value.Age < age).ToArray().Length);
            Assert.AreEqual(age, cache.Where(x => x.Value.Address.Zip < age).ToArray().Length);
            Assert.AreEqual(19, cache.Where(x => x.Value.Age > age && x.Value.Age < 30).ToArray().Length);
            Assert.AreEqual(20, cache.Where(x => x.Value.Age > age).Count(x => x.Value.Age < 30 || x.Value.Age == 50));
            Assert.AreEqual(key, cache.Where(x => x.Key < key).ToArray().Length);
            Assert.AreEqual(key, cache.Where(x => -x.Key > -key).ToArray().Length);

            Assert.AreEqual(1, GetRoleCache().AsCacheQueryable().Where(x => x.Key.Foo < 2).ToArray().Length);
            var cacheEntries = GetRoleCache().AsCacheQueryable().Where(x => x.Key.Bar > 2 && x.Value.Name != "11")
                .ToArray();
            Assert.AreEqual(3, cacheEntries.Length);
        }

        /// <summary>
        /// Tests the union.
        /// </summary>
        [Test]
        public void TestUnion()
        {
            // Direct union
            var persons = GetPersonCache().AsCacheQueryable();
            var persons2 = GetSecondPersonCache().AsCacheQueryable();

            var res = persons.Union(persons2).ToArray();

            Assert.AreEqual(PersonCount * 2, res.Length);

            // Subquery
            var roles = GetRoleCache().AsCacheQueryable().Select(x => -x.Key.Foo);
            var ids = GetPersonCache().AsCacheQueryable().Select(x => x.Key).Union(roles).ToArray();

            Assert.AreEqual(RoleCount + PersonCount, ids.Length);
        }

        /// <summary>
        /// Tests intersect.
        /// </summary>
        [Test]
        public void TestIntersect()
        {
            // Direct intersect
            var persons = GetPersonCache().AsCacheQueryable();
            var persons2 = GetSecondPersonCache().AsCacheQueryable();

            var res = persons.Intersect(persons2).ToArray();

            Assert.AreEqual(0, res.Length);

            // Subquery
            var roles = GetRoleCache().AsCacheQueryable().Select(x => x.Key.Foo);
            var ids = GetPersonCache().AsCacheQueryable().Select(x => x.Key).Intersect(roles).ToArray();

            Assert.AreEqual(RoleCount, ids.Length);
        }

        /// <summary>
        /// Tests except.
        /// </summary>
        [Test]
        public void TestExcept()
        {
            // Direct except
            var persons = GetPersonCache().AsCacheQueryable();
            var persons2 = GetSecondPersonCache().AsCacheQueryable();

            var res = persons.Except(persons2).ToArray();

            Assert.AreEqual(PersonCount, res.Length);

            // Subquery
            var roles = GetRoleCache().AsCacheQueryable().Select(x => x.Key.Foo);
            var ids = GetPersonCache().AsCacheQueryable().Select(x => x.Key).Except(roles).ToArray();

            Assert.AreEqual(PersonCount - RoleCount, ids.Length);
        }

        /// <summary>
        /// Tests ordering.
        /// </summary>
        [Test]
        public void TestOrdering()
        {
            var persons = GetPersonCache().AsCacheQueryable()
                .OrderByDescending(x => x.Key)
                .ThenBy(x => x.Value.Age)
                .ToArray();

            Assert.AreEqual(Enumerable.Range(0, PersonCount).Reverse().ToArray(),
                persons.Select(x => x.Key).ToArray());

            var personsByOrg = GetPersonCache().AsCacheQueryable()
                .Join(GetOrgCache().AsCacheQueryable(), p => p.Value.OrganizationId, o => o.Value.Id,
                    (p, o) => new
                    {
                        PersonId = p.Key,
                        PersonName = p.Value.Name.ToUpper(),
                        OrgName = o.Value.Name
                    })
                .OrderBy(x => x.OrgName.ToLower())
                .ThenBy(x => x.PersonName)
                .ToArray();

            var expectedIds = Enumerable.Range(0, PersonCount)
                .OrderBy(x => (x % 2).ToString())
                .ThenBy(x => x.ToString())
                .ToArray();

            var actualIds = personsByOrg.Select(x => x.PersonId).ToArray();

            Assert.AreEqual(expectedIds, actualIds);
        }
    }
}
