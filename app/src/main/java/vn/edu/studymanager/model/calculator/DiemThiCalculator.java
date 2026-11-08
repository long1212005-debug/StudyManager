package vn.edu.studymanager.model.calculator;

/**
 * Grade calculation without Android dependencies. All grades use a 0..10 scale.
 * Weights are fractions, not percentages, and must total 1.0.
 * No letter-grade thresholds are assumed here.
 */
public final class DiemThiCalculator {
    private static final double EPS = 1e-6;

    private DiemThiCalculator() {}

    public static double tinhDiemTongKet(
            double tx1, double tx2, Double tx3, double thi,
            double w1, double w2, double w3, double wThi) {
        validateInputs(tx1, tx2, tx3, w1, w2, w3, wThi);
        validateGrade("diem thi", thi);
        return tx1 * w1 + tx2 * w2 + (tx3 == null ? 0 : tx3 * w3) + thi * wThi;
    }

    /**
     * Returns the exact exam score required; a result > 10 means unreachable.
     * Rounding to the grading precision is the caller's responsibility.
     */
    public static double tinhDiemThiCanDat(
            double tx1, double tx2, Double tx3, double mucTieu,
            double w1, double w2, double w3, double wThi) {
        validateInputs(tx1, tx2, tx3, w1, w2, w3, wThi);
        validateGrade("muc tieu", mucTieu);
        if (wThi <= EPS) {
            throw new IllegalArgumentException("Trong so diem thi phai lon hon 0");
        }
        double completed = tx1 * w1 + tx2 * w2 + (tx3 == null ? 0 : tx3 * w3);
        return Math.max(0, (mucTieu - completed) / wThi);
    }

    private static void validateInputs(double tx1, double tx2, Double tx3,
                                       double w1, double w2, double w3, double wThi) {
        validateGrade("TX1", tx1);
        validateGrade("TX2", tx2);
        if (tx3 != null) validateGrade("TX3", tx3);
        validateWeight(w1); validateWeight(w2); validateWeight(w3); validateWeight(wThi);
        if (Math.abs(w1 + w2 + w3 + wThi - 1) > EPS) {
            throw new IllegalArgumentException("Tong trong so phai bang 1");
        }
        if (tx3 == null && w3 > EPS) {
            throw new IllegalArgumentException("Thieu TX3 trong khi trong so TX3 lon hon 0");
        }
    }

    private static void validateGrade(String label, double value) {
        if (!Double.isFinite(value) || value < 0 || value > 10) {
            throw new IllegalArgumentException(label + " phai trong [0, 10]");
        }
    }

    private static void validateWeight(double weight) {
        if (!Double.isFinite(weight) || weight < 0 || weight > 1) {
            throw new IllegalArgumentException("Trong so phai trong [0, 1]");
        }
    }
}
