/*
 * Copyright 2014 The Closure Compiler Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.javascript.jscomp.newtypes;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

/** A persistent set that simply wraps Clojure's implementation */
public class ClojurePersistentHashSet<K> extends PersistentSet<K> {
  private static Method cons;
  private static Method disjoin;
  private final Set set;

  private ClojurePersistentHashSet(Set s) {
    this.set = s;
  }

  public static <K> PersistentSet<K> create(Class<? extends Set> cls)  {
    try {
      cons = cls.getDeclaredMethod("cons", Object.class);
      disjoin = cls.getDeclaredMethod("disjoin", Object.class);
      Set m = (Set) cls.getDeclaredField("EMPTY").get(null);
      return new ClojurePersistentHashSet<>(m);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  public PersistentSet<K> with(K key) {
    try {
      Set s = (Set) cons.invoke(set, key);
      return new ClojurePersistentHashSet<>(s);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  public PersistentSet<K> without(K key) {
    try {
      Set s = (Set) disjoin.invoke(set, key);
      return new ClojurePersistentHashSet<>(s);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean contains(Object key) {
    return set.contains(key);
  }

  @Override
  public int size() {
    return set.size();
  }

  @Override
  public boolean isEmpty() {
    return set.isEmpty();
  }

  @Override
  public Iterator<K> iterator() {
    return set.iterator();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ClojurePersistentHashSet) {
      ClojurePersistentHashSet ps = (ClojurePersistentHashSet) o;
      return this.set.equals(ps.set);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return set.hashCode();
  }
}
