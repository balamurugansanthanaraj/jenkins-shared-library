"""
Tests for the string_utils module.
"""

import pytest
from sample_python_lib.string_utils import StringUtils


class TestStringUtils:
    """Test cases for the StringUtils class."""

    def setup_method(self):
        """Set up test fixtures."""
        self.utils = StringUtils()

    def test_reverse(self):
        """Test string reversal."""
        assert self.utils.reverse("hello") == "olleh"
        assert self.utils.reverse("racecar") == "racecar"
        assert self.utils.reverse("") == ""
        assert self.utils.reverse("a") == "a"
        assert self.utils.reverse("12345") == "54321"

    def test_palindrome(self):
        """Test palindrome detection."""
        assert self.utils.palindrome("racecar") is True
        assert self.utils.palindrome("A man a plan a canal Panama") is True
        assert self.utils.palindrome("hello") is False
        assert self.utils.palindrome("") is True
        assert self.utils.palindrome("a") is True
        assert self.utils.palindrome("Madam, I'm Adam") is True

    def test_count_vowels(self):
        """Test vowel counting."""
        assert self.utils.count_vowels("hello") == 2
        assert self.utils.count_vowels("world") == 1
        assert self.utils.count_vowels("aeiou") == 5
        assert self.utils.count_vowels("AEIOU") == 5
        assert self.utils.count_vowels("bcdfg") == 0
        assert self.utils.count_vowels("") == 0

    def test_count_consonants(self):
        """Test consonant counting."""
        assert self.utils.count_consonants("hello") == 3
        assert self.utils.count_consonants("world") == 4
        assert self.utils.count_consonants("aeiou") == 0
        assert self.utils.count_consonants("bcdfg") == 5
        assert self.utils.count_consonants("") == 0

    def test_capitalize_words(self):
        """Test word capitalization."""
        assert self.utils.capitalize_words("hello world") == "Hello World"
        assert self.utils.capitalize_words("python programming") == "Python Programming"
        assert self.utils.capitalize_words("") == ""
        assert self.utils.capitalize_words("a") == "A"
        assert self.utils.capitalize_words("hello") == "Hello"

    def test_remove_duplicates(self):
        """Test duplicate character removal."""
        assert self.utils.remove_duplicates("hello") == "helo"
        assert self.utils.remove_duplicates("mississippi") == "misp"
        assert self.utils.remove_duplicates("") == ""
        assert self.utils.remove_duplicates("a") == "a"
        assert self.utils.remove_duplicates("aaa") == "a"

    def test_word_count(self):
        """Test word counting."""
        assert self.utils.word_count("hello world") == 2
        assert self.utils.word_count("python programming language") == 3
        assert self.utils.word_count("") == 0
        assert self.utils.word_count("hello") == 1
        assert self.utils.word_count("   hello   world   ") == 2

    def test_char_count(self):
        """Test character counting."""
        assert self.utils.char_count("hello", "l") == 2
        assert self.utils.char_count("mississippi", "s") == 4
        assert self.utils.char_count("hello", "x") == 0
        assert self.utils.char_count("", "a") == 0

    def test_is_anagram(self):
        """Test anagram detection."""
        assert self.utils.is_anagram("listen", "silent") is True
        assert self.utils.is_anagram("hello", "world") is False
        assert self.utils.is_anagram("", "") is True
        assert self.utils.is_anagram("a", "a") is True
        assert self.utils.is_anagram("a", "b") is False
        assert self.utils.is_anagram("debit card", "bad credit") is True

    def test_extract_numbers(self):
        """Test number extraction."""
        assert self.utils.extract_numbers("hello123world456") == [123, 456]
        assert self.utils.extract_numbers("no numbers here") == []
        assert self.utils.extract_numbers("") == []
        assert self.utils.extract_numbers("123") == [123]
        assert self.utils.extract_numbers("1 2 3") == [1, 2, 3]

    def test_extract_emails(self):
        """Test email extraction."""
        emails = self.utils.extract_emails("Contact us at test@example.com or support@company.org")
        assert emails == ["test@example.com", "support@company.org"]
        
        assert self.utils.extract_emails("no emails here") == []
        assert self.utils.extract_emails("") == []
        assert self.utils.extract_emails("test@example.com") == ["test@example.com"]

    def test_truncate(self):
        """Test string truncation."""
        assert self.utils.truncate("hello world", 5) == "he..."
        assert self.utils.truncate("hello world", 10) == "hello w..."
        assert self.utils.truncate("hello world", 11) == "hello world"
        assert self.utils.truncate("hello world", 20) == "hello world"
        assert self.utils.truncate("", 5) == ""

    def test_truncate_custom_suffix(self):
        """Test string truncation with custom suffix."""
        assert self.utils.truncate("hello world", 5, "***") == "he***"
        assert self.utils.truncate("hello world", 10, "") == "hello worl"

    def test_slugify(self):
        """Test string slugification."""
        assert self.utils.slugify("Hello World") == "hello-world"
        assert self.utils.slugify("Python Programming") == "python-programming"
        assert self.utils.slugify("") == ""
        assert self.utils.slugify("a") == "a"
        assert self.utils.slugify("   hello   world   ") == "hello-world"
        assert self.utils.slugify("Special@Characters!") == "specialcharacters"

    def test_edge_cases(self):
        """Test edge cases and boundary conditions."""
        # Empty strings
        assert self.utils.reverse("") == ""
        assert self.utils.palindrome("") is True
        assert self.utils.count_vowels("") == 0
        assert self.utils.word_count("") == 0
        
        # Single characters
        assert self.utils.reverse("a") == "a"
        assert self.utils.palindrome("a") is True
        assert self.utils.count_vowels("a") == 1
        assert self.utils.word_count("a") == 1
        
        # Whitespace
        assert self.utils.reverse("   ") == "   "
        assert self.utils.palindrome("   ") is True
        assert self.utils.count_vowels("   ") == 0
        assert self.utils.word_count("   ") == 0

    def test_unicode_support(self):
        """Test Unicode character support."""
        assert self.utils.reverse("café") == "éfac"
        assert self.utils.count_vowels("café") == 2
        assert self.utils.word_count("café au lait") == 3
        assert self.utils.capitalize_words("café au lait") == "Café Au Lait"

    def test_case_sensitivity(self):
        """Test case sensitivity in various operations."""
        assert self.utils.reverse("Hello") == "olleH"
        assert self.utils.count_vowels("Hello") == 2
        assert self.utils.count_consonants("Hello") == 3
        assert self.utils.capitalize_words("hello world") == "Hello World"
        assert self.utils.is_anagram("Listen", "Silent") is True 