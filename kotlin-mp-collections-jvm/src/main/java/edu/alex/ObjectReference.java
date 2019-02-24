package edu.alex;

interface ObjectReference<T> {
    T get();

    @SuppressWarnings("unchecked")
    static <T> ObjectReference<T> wrap(final T object) {
        if(object == null) {
            return (ObjectReference) nullReference;
        }
        if(object instanceof String || object instanceof Integer || object instanceof Boolean) {
            return new PrimitiveReference(object);
        }

        return new NonPrimitiveReference(object);
    }

    ObjectReference<Object> nullReference = new ObjectReference<Object>() {
        @Override
        public Object get() {
            return null;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return obj == null || obj instanceof ObjectReference && ((ObjectReference) obj).get() == null;
        }
    };

    class PrimitiveReference<T> implements ObjectReference<T> {
        private final T ref;

        PrimitiveReference(final T ref) {
            this.ref = ref;
        }

        @Override
        public T get() {
            return this.ref;
        }

        @Override
        public int hashCode() {
            return this.ref.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ObjectReference && this.ref.equals( ((ObjectReference) obj).get() );
        }
    }

    class NonPrimitiveReference<T> implements ObjectReference<T> {
        private final T ref;

        NonPrimitiveReference(final T ref) {
            this.ref = ref;
        }

        @Override
        public T get() {
            return this.ref;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this.ref);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ObjectReference && this.ref == ((ObjectReference) obj).get();
        }
    }
}