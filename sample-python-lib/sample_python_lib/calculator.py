"""
Calculator module providing basic mathematical operations.
"""

import math
from typing import Union, Optional


class Calculator:
    """A simple calculator class with basic mathematical operations."""

    def __init__(self):
        """Initialize the calculator."""
        pass

    def add(self, a: Union[int, float], b: Union[int, float]) -> Union[int, float]:
        """Add two numbers.

        Args:
            a: First number
            b: Second number

        Returns:
            Sum of the two numbers
        """
        return a + b

    def subtract(self, a: Union[int, float], b: Union[int, float]) -> Union[int, float]:
        """Subtract two numbers.

        Args:
            a: First number
            b: Second number

        Returns:
            Difference of the two numbers
        """
        return a - b

    def multiply(self, a: Union[int, float], b: Union[int, float]) -> Union[int, float]:
        """Multiply two numbers.

        Args:
            a: First number
            b: Second number

        Returns:
            Product of the two numbers
        """
        return a * b

    def divide(self, a: Union[int, float], b: Union[int, float]) -> float:
        """Divide two numbers.

        Args:
            a: First number
            b: Second number

        Returns:
            Quotient of the two numbers

        Raises:
            ZeroDivisionError: If b is zero
        """
        if b == 0:
            raise ZeroDivisionError("Cannot divide by zero")
        return a / b

    def power(self, base: Union[int, float], exponent: Union[int, float]) -> Union[int, float]:
        """Raise a number to a power.

        Args:
            base: The base number
            exponent: The exponent

        Returns:
            The result of base raised to the power of exponent
        """
        return math.pow(base, exponent)

    def sqrt(self, number: Union[int, float]) -> float:
        """Calculate the square root of a number.

        Args:
            number: The number to find the square root of

        Returns:
            The square root of the number

        Raises:
            ValueError: If number is negative
        """
        if number < 0:
            raise ValueError("Cannot calculate square root of negative number")
        return math.sqrt(number)

    def factorial(self, n: int) -> int:
        """Calculate the factorial of a number.

        Args:
            n: The number to calculate factorial for

        Returns:
            The factorial of the number

        Raises:
            ValueError: If n is negative
        """
        if n < 0:
            raise ValueError("Cannot calculate factorial of negative number")
        if n == 0 or n == 1:
            return 1
        return n * self.factorial(n - 1)

    def percentage(self, value: Union[int, float], total: Union[int, float]) -> float:
        """Calculate percentage of a value relative to total.

        Args:
            value: The value to calculate percentage for
            total: The total value

        Returns:
            The percentage as a float

        Raises:
            ZeroDivisionError: If total is zero
        """
        if total == 0:
            raise ZeroDivisionError("Total cannot be zero")
        return (value / total) * 100

    def average(self, numbers: list[Union[int, float]]) -> float:
        """Calculate the average of a list of numbers.

        Args:
            numbers: List of numbers

        Returns:
            The average of the numbers

        Raises:
            ValueError: If the list is empty
        """
        if not numbers:
            raise ValueError("Cannot calculate average of empty list")
        return sum(numbers) / len(numbers) 