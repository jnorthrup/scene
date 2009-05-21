package scene;

import com.thoughtworks.xstream.persistence.StreamStrategy;

/**
 * User: jim
 * Date: May 13, 2009
 * Time: 7:11:18 AM
 */
public class Pair<$1, $2> {
    Object[] v;
 
    public Pair(Object... v) {
        this.v = v;
    }

    final $1 $1() {
        return ($1) v[0];

    }
    final $2 $2() {
        return ($2) v[1];

    }

    final $1 $1($1 $1) {
        return
                        v.length > 1 ?
                                ($1) (v[0] = $1)
                                : ($1) (this.v = new Object[]
                                {$1, $2() })[0];

    }

    final $2 $2($2 $2) {
        return
                        v.length > 1 ?
                                ($2) (v[1] = $2)
                                : ($2) (this.v = new Object[]
                                {$1(), $2 })[1];

    }

}
