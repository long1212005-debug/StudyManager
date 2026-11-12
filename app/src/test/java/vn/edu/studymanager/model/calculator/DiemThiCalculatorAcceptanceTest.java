package vn.edu.studymanager.model.calculator;

import org.junit.Test;
import static org.junit.Assert.*;

/** Regression tests added for Week 5 acceptance. */
public class DiemThiCalculatorAcceptanceTest {
    private static final double DELTA = 1e-7;

    @Test public void allTenGradesRequireTenForTenTarget() {
        assertEquals(10, DiemThiCalculator.tinhDiemThiCanDat(10,10,10d,10,.2,.2,.2,.4), DELTA);
    }

    @Test public void scoreWithinRangeCanNeedFractionalExam() {
        assertEquals(4.25, DiemThiCalculator.tinhDiemThiCanDat(8,7,9d,6.5,.2,.2,.2,.4), DELTA);
    }

    @Test public void noTx3WhenWeightZero() {
        assertEquals(7.5, DiemThiCalculator.tinhDiemTongKet(8,7,null,7.5,.25,.25,0,.5), DELTA);
    }

    @Test public void zeroGoalNeedsNoExam() {
        assertEquals(0, DiemThiCalculator.tinhDiemThiCanDat(0,0,0d,0,.2,.2,.2,.4), DELTA);
    }

    @Test public void unreachableTargetIsNotClampedToTen() {
        assertTrue(DiemThiCalculator.tinhDiemThiCanDat(0,0,0d,9,.2,.2,.2,.4) > 10);
    }

    @Test(expected=IllegalArgumentException.class) public void cannotEnterEleven() {
        DiemThiCalculator.tinhDiemTongKet(11,7,9d,6,.2,.2,.2,.4);
    }

    @Test(expected=IllegalArgumentException.class) public void missingThirdScoreWithPositiveWeight() {
        DiemThiCalculator.tinhDiemThiCanDat(8,7,null,6.5,.2,.2,.2,.4);
    }

    @Test(expected=IllegalArgumentException.class) public void infiniteInputRejected() {
        DiemThiCalculator.tinhDiemThiCanDat(Double.POSITIVE_INFINITY,7,9d,6.5,.2,.2,.2,.4);
    }
}
