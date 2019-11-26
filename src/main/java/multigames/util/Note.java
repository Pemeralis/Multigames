package multigames.util;

public class Note {
    public static final float Fs0, Gf0;
    public static final float G1, Gs1, Af1, A1, As1, Bf1, B1, C1, Cs1, Df1, D1, Ds1, Ef1, E1, F1, Fs1, Gf1;
    public static final float G2, Gs2, Af2, A2, As2, Bf2, B2, C2, Cs2, Df2, D2, Ds2, Ef2, E2, F2, Fs2, Gf2;

    static {
        Fs0 = 0.5f;
        Gf0 = Fs0;

        G1 = note(-11);
        Gs1 = note(-10);
        Af1 = Gs1;
        A1 = note(-9);
        As1 = note(-8);
        Bf1 = As1;
        B1 = note(-7);
        C1 = note(-6);
        Cs1 = note(-5);
        Df1 = Cs1;
        D1 = note(-4);
        Ds1 = note(-3);
        Ef1 = Ds1;
        E1 = note(-2);
        F1 = note(-1);
        Fs1 = 1f;
        Gf1 = Fs1;

        G2 = note(1);
        Gs2 = note(2);
        Af2 = Gs2;
        A2 = note(3);
        As2 = note(4);
        Bf2 = As2;
        B2 = note(5);
        C2 = note(6);
        Cs2 = note(7);
        Df2 = Cs2;
        D2 = note(8);
        Ds2 = note(9);
        Ef2 = Ds2;
        E2 = note(10);
        F2 = note(11);
        Fs2 = 2f;
        Gf2 = Fs2;
    }
    private static float note(int a) {
        return (float) Math.pow(2, (double) a/12);
    }
}
