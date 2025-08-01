"""
Tests for the calculator module.
"""

import pytest
from sample_python_lib.calculator import Calculator


class TestCalculator:
    """Test cases for the Calculator class."""

    def setup_method(self):
        """Set up test fixtures."""
        self.calc = Calculator()

    def test_add(self):
        """Test addition operation."""
        assert self.calc.add(5, 3) == 8
        assert self.calc.add(-1, 1) == 0
        assert self.calc.add(0, 0) == 0
        assert self.calc.add(3.5, 2.5) == 6.0

    def test_subtract(self):
        """Test subtraction operation."""
        assert self.calc.subtract(10, 4) == 6
        assert self.calc.subtract(5, 10) == -5
        assert self.calc.subtract(0, 0) == 0
        assert self.calc.subtract(3.5, 1.5) == 2.0

    def test_multiply(self):
        """Test multiplication operation."""
        assert self.calc.multiply(3, 7) == 21
        assert self.calc.multiply(-2, 3) == -6
        assert self.calc.multiply(0, 5) == 0
        assert self.calc.multiply(2.5, 2) == 5.0

    def test_divide(self):
        """Test division operation."""
        assert self.calc.divide(15, 3) == 5.0
        assert self.calc.divide(10, 2) == 5.0
        assert self.calc.divide(7, 2) == 3.5
        assert self.calc.divide(0, 5) == 0.0

    def test_divide_by_zero(self):
        """Test division by zero raises exception."""
        with pytest.raises(ZeroDivisionError):
            self.calc.divide(10, 0)

    def test_power(self):
        """Test power operation."""
        assert self.calc.power(2, 8) == 256
        assert self.calc.power(3, 3) == 27
        assert self.calc.power(5, 0) == 1
        assert self.calc.power(2, -1) == 0.5

    def test_sqrt(self):
        """Test square root operation."""
        assert self.calc.sqrt(16) == 4.0
        assert self.calc.sqrt(25) == 5.0
        assert self.calc.sqrt(0) == 0.0
        assert self.calc.sqrt(2) == pytest.approx(1.4142135623730951)

    def test_sqrt_negative(self):
        """Test square root of negative number raises exception."""
        with pytest.raises(ValueError):
            self.calc.sqrt(-1)

    def test_factorial(self):
        """Test factorial operation."""
        assert self.calc.factorial(0) == 1
        assert self.calc.factorial(1) == 1
        assert self.calc.factorial(5) == 120
        assert self.calc.factorial(10) == 3628800

    def test_factorial_negative(self):
        """Test factorial of negative number raises exception."""
        with pytest.raises(ValueError):
            self.calc.factorial(-1)

    def test_percentage(self):
        """Test percentage calculation."""
        assert self.calc.percentage(25, 100) == 25.0
        assert self.calc.percentage(50, 200) == 25.0
        assert self.calc.percentage(0, 100) == 0.0
        assert self.calc.percentage(100, 100) == 100.0

    def test_percentage_zero_total(self):
        """Test percentage with zero total raises exception."""
        with pytest.raises(ZeroDivisionError):
            self.calc.percentage(25, 0)

    def test_average(self):
        """Test average calculation."""
        assert self.calc.average([1, 2, 3, 4, 5]) == 3.0
        assert self.calc.average([10, 20, 30]) == 20.0
        assert self.calc.average([0]) == 0.0
        assert self.calc.average([1.5, 2.5, 3.5]) == 2.5

    def test_average_empty_list(self):
        """Test average of empty list raises exception."""
        with pytest.raises(ValueError):
            self.calc.average([])

    def test_mixed_operations(self):
        """Test multiple operations together."""
        result = self.calc.add(
            self.calc.multiply(2, 3),
            self.calc.divide(10, 2)
        )
        assert result == 11.0

    def test_floating_point_precision(self):
        """Test floating point precision."""
        result = self.calc.add(0.1, 0.2)
        assert result == pytest.approx(0.3, rel=1e-10)

    def test_large_numbers(self):
        """Test operations with large numbers."""
        large_num = 999999999
        assert self.calc.add(large_num, 1) == 1000000000
        assert self.calc.multiply(large_num, 2) == 1999999998

    def test_type_consistency(self):
        """Test that operations return consistent types."""
        # Integer operations should return integers when possible
        assert isinstance(self.calc.add(1, 2), int)
        assert isinstance(self.calc.multiply(2, 3), int)
        
        # Division always returns float
        assert isinstance(self.calc.divide(6, 2), float)
        
        # Float operations return float
        assert isinstance(self.calc.add(1.5, 2.5), float) 