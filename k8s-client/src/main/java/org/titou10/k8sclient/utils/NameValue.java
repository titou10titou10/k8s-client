package org.titou10.k8sclient.utils;

import javax.annotation.Nullable;

public final class NameValue<N, V> {

   @Nullable
   public final N name;

   @Nullable
   public final V value;

   public static <N, V> NameValue<N, V> of(@Nullable N name, @Nullable V value) {
      return new NameValue<N, V>(name, value);
   }

   public NameValue(@Nullable N name, @Nullable V value) {
      this.name = name;
      this.value = value;
   }

   @Nullable
   public N getName() {
      return name;
   }

   @Nullable
   public V getValue() {
      return value;
   }

   @Override
   public String toString() {
      return "NameValue['" + name + "'='" + value + "']";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      NameValue<?, ?> other = (NameValue<?, ?>) obj;
      if (name == null) {
         if (other.name != null) {
            return false;
         }
      } else
         if (!name.equals(other.name)) {
            return false;
         }
      return true;
   }

}
