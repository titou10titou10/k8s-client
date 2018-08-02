package org.titou10.k8sclient.extension;

public class SubstitutionsExtension {

   private String name;
   private String value;

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("SubstitutionExtension [name=");
      builder.append(name);
      builder.append(", value=");
      builder.append(value);
      builder.append("]");
      return builder.toString();
   }

   // ---------------
   // Constructors
   // ---------------
   // public SubstitutionsExtension() {
   // }
   //
   public SubstitutionsExtension(String name) {
      System.out.println(">>>>> name=" + name);
      this.name = name;
   }

   // ---------------
   // hashCode / equals
   // ---------------

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
      SubstitutionsExtension other = (SubstitutionsExtension) obj;
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
   // ---------------
   // Getters/Setters
   // ---------------

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

}
