package inc.glamdring.vtables;

class edgeString$CharSequence implements edge<String, CharSequence> {

    public String left(edge<String, CharSequence> p) {
        return p.right(p).toString();
    }

    public CharSequence right(edge<String, CharSequence> p) {
        return p.left(p);
    }

    public edge<String, CharSequence> midpoint(final String s, final CharSequence charSequence) {
        return new edge<String, CharSequence>() {
            public String left(edge<String, CharSequence> p) {
                return s;
            }

            public CharSequence right(edge<String, CharSequence> p) {
                return s;
            }

            public edge<String, CharSequence> midpoint(String s, CharSequence charSequence) {
                return edgeString$CharSequence.this.midpoint(s, charSequence);
            }

            public String reify(ptr void$) {
                return s;
            }
        };
    }

    public String reify(ptr void$) {
        return void$.l$().asCharBuffer().toString();  //todo: verify for a purpose
    }
}