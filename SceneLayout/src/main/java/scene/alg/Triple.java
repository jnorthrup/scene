package scene.alg;

import static scene.SceneLayoutApp.XSTREAM;

/**
 * Copyright hideftvads.com 2009 all rights reserved.
 * <p/>
 * User: jim
 * Date: May 15, 2009
 * Time: 8:21:44 PM
 */
public class Triple<$1, $2, $3> {
    Object[] v;


    static {
        XSTREAM.aliasType("triple", Triple.class);

        XSTREAM.useAttributeFor("v", Triple.class);
    }

    public Triple(Object... v) {
        this.v = v;
    }

    public $1 $1() {
        return ($1) v[0];

    }

    public $2 $2() {
        return ($2) v[1];

    }

    public $3 $3() {
        return ($3) v[2];
    }

    public $1 $1($1 $1) {
        return
                v.length > 1 ?
                        ($1) (v[0] = $1)
                        : ($1) (this.v = new Object[]
                        {$1, $2(), $3()})[0];

    }

    public $2 $2($2 $2) {
        return
                v.length > 1 ?
                        ($2) (v[1] = $2)
                        : ($2) (this.v = new Object[]
                        {$1(), $2, $3()})[1];

    }

    public $3 $3($3 $3) {
        return
                v.length > 2 ?
                        ($3) (v[2] = $3)
                        : ($3) (this.v = new Object[]
                        {$1(), $2(), $3})[2];
    }
}
