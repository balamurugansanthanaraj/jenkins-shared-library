"""
String utilities module providing common string manipulation functions.
"""

import re
from typing import List, Optional


class StringUtils:
    """A utility class for string manipulation operations."""

    def __init__(self):
        """Initialize the string utilities."""
        pass

    def reverse(self, text: str) -> str:
        """Reverse a string.

        Args:
            text: The string to reverse

        Returns:
            The reversed string
        """
        return text[::-1]

    def palindrome(self, text: str) -> bool:
        """Check if a string is a palindrome.

        Args:
            text: The string to check

        Returns:
            True if the string is a palindrome, False otherwise
        """
        # Remove non-alphanumeric characters and convert to lowercase
        cleaned = re.sub(r"[^a-zA-Z0-9]", "", text.lower())
        return cleaned == cleaned[::-1]

    def count_vowels(self, text: str) -> int:
        """Count the number of vowels in a string.

        Args:
            text: The string to count vowels in

        Returns:
            The number of vowels in the string
        """
        vowels = "aeiouAEIOU"
        return sum(1 for char in text if char in vowels)

    def count_consonants(self, text: str) -> int:
        """Count the number of consonants in a string.

        Args:
            text: The string to count consonants in

        Returns:
            The number of consonants in the string
        """
        consonants = "bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ"
        return sum(1 for char in text if char in consonants)

    def capitalize_words(self, text: str) -> str:
        """Capitalize the first letter of each word in a string.

        Args:
            text: The string to capitalize

        Returns:
            The string with each word capitalized
        """
        return text.title()

    def remove_duplicates(self, text: str) -> str:
        """Remove duplicate characters from a string.

        Args:
            text: The string to remove duplicates from

        Returns:
            The string with duplicate characters removed
        """
        seen = set()
        result = ""
        for char in text:
            if char not in seen:
                seen.add(char)
                result += char
        return result

    def word_count(self, text: str) -> int:
        """Count the number of words in a string.

        Args:
            text: The string to count words in

        Returns:
            The number of words in the string
        """
        return len(text.split())

    def char_count(self, text: str, char: str) -> int:
        """Count the occurrences of a specific character in a string.

        Args:
            text: The string to search in
            char: The character to count

        Returns:
            The number of occurrences of the character
        """
        return text.count(char)

    def is_anagram(self, text1: str, text2: str) -> bool:
        """Check if two strings are anagrams.

        Args:
            text1: First string
            text2: Second string

        Returns:
            True if the strings are anagrams, False otherwise
        """
        # Remove spaces and convert to lowercase
        text1_clean = re.sub(r"\s", "", text1.lower())
        text2_clean = re.sub(r"\s", "", text2.lower())
        
        # Sort characters and compare
        return sorted(text1_clean) == sorted(text2_clean)

    def extract_numbers(self, text: str) -> List[int]:
        """Extract all numbers from a string.

        Args:
            text: The string to extract numbers from

        Returns:
            List of integers found in the string
        """
        numbers = re.findall(r"\d+", text)
        return [int(num) for num in numbers]

    def extract_emails(self, text: str) -> List[str]:
        """Extract all email addresses from a string.

        Args:
            text: The string to extract emails from

        Returns:
            List of email addresses found in the string
        """
        email_pattern = r"\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b"
        return re.findall(email_pattern, text)

    def truncate(self, text: str, length: int, suffix: str = "...") -> str:
        """Truncate a string to a specified length.

        Args:
            text: The string to truncate
            length: The maximum length
            suffix: The suffix to add if truncated

        Returns:
            The truncated string
        """
        if len(text) <= length:
            return text
        return text[:length - len(suffix)] + suffix

    def slugify(self, text: str) -> str:
        """Convert a string to a URL-friendly slug.

        Args:
            text: The string to convert

        Returns:
            The slugified string
        """
        # Convert to lowercase and replace spaces with hyphens
        slug = re.sub(r"[^\w\s-]", "", text.lower())
        slug = re.sub(r"[-\s]+", "-", slug)
        return slug.strip("-") 