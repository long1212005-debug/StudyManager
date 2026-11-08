package vn.edu.studymanager.model.calculator;

import org.junit.Test;
import static org.junit.Assert.*;

public class DiemThiCalculatorTest {
    private static final double E = 1e-8;
    @Test public void weightedGradeUsingSeedConfig() {
        assertEquals(7.2, DiemThiCalculator.tinhDiemTongKet(8,7,9d,6,.2,.2,.2,.4), E);
    }
    @Test public void examRequiredForTarget() {
        assertEquals(4.25, DiemThiCalculator.tinhDiemThiCanDat(8,7,9d,6.5,.2,.2,.2,.4), E);
    }
    @Test public void examRequirementAboveTenIsNotHidden() {
        assertTrue(DiemThiCalculator.tinhDiemThiCanDat(0,0,0d,8,.2,.2,.2,.4) > 10);
    }
    @Test public void examRequirementFloorsAtZero() {
        assertEquals(0, DiemThiCalculator.tinhDiemThiCanDat(10,10,10d,2,.2,.2,.2,.4), E);
    }
    @Test public void validBoundaries() {
        assertEquals(0, DiemThiCalculator.tinhDiemTongKet(0,0,0d,0,.2,.2,.2,.4), E);
        assertEquals(10, DiemThiCalculator.tinhDiemTongKet(10,10,10d,10,.2,.2,.2,.4), E);
    }
    @Test public void tx3CanBeAbsentWhenWeightZero() {
        assertEquals(7.5, DiemThiCalculator.tinhDiemTongKet(8,7,null,7.5,.25,.25,0,.5), E);
    }
    @Test(expected=IllegalArgumentException.class) public void tx3MissingWithPositiveWeight() {
        DiemThiCalculator.tinhDiemTongKet(8,7,null,6,.2,.2,.2,.4);
    }
    @Test(expected=IllegalArgumentException.class) public void scoreNegative() {
        DiemThiCalculator.tinhDiemTongKet(-1,7,9d,6,.2,.2,.2,.4);
    }
    @Test(expected=IllegalArgumentException.class) public void scoreOverTen() {
        DiemThiCalculator.tinhDiemTongKet(11,7,9d,6,.2,.2,.2,.4);
    }
    @Test(expected=IllegalArgumentException.class) public void weightSumInvalid() {
        DiemThiCalculator.tinhDiemTongKet(8,7,9d,6,.2,.2,.2,.3);
    }
    @Test(expected=IllegalArgumentException.class) public void weightNegative() {
        DiemThiCalculator.tinhDiemTongKet(8,7,9d,6,-.1,.3,.2,.6);
    }
    @Test(expected=IllegalArgumentException.class) public void noExamWeight() {
        DiemThiCalculator.tinhDiemThiCanDat(8,7,9d,8,.4,.3,.3,0);
    }
    @Test(expected=IllegalArgumentException.class) public void nanValue() {
        DiemThiCalculator.tinhDiemTongKet(Double.NaN,7,9d,6,.2,.2,.2,.4);
    }
    @Test(expected=IllegalArgumentException.class) public void invalidGoal() {
        DiemThiCalculator.tinhDiemThiCanDat(8,7,9d,10.5,.2,.2,.2,.4);
    }
}
