This project is a autograding and testing framework for java programs, written in java. It is very similar to junits, however it uses json to outline comments and point values with the tests. The json can also define expected class structure, and in the future, the class structure of the program being tested can also be evaluated against the class structure outline in the json.

Currently the program can parse the json outlining the tests and class structure, run tests (it is *almost* completely backwards-compatible with junits tests, just change your imports), and parse a provided java class containing tests (i.e. a class written using junits tests) to generate a json template for all of the tests contained in the class.

Usage:

To parse a pre-written test class and generate the json template:

`java -cp .:/path/to/autograder.jar edu.gatech.cs1331.Parser [class_name] [output.json]`

To run a test file with a provided json:

`java -jar autograder.jar -t [class_filename] -j [json_filename]`

or

`java -cp .:/path/to/autograder.jar edu.gatech.cs1331.Application -t [class_name] -j [json_filename]`


Writing tests:

If you have a junits test class written already, you (most likely) just change the imports and it will run with this program. Annotations are in package edu.gatech.cs1331.annotations, and Asserts are in edu.gatech.cs1331.Assert.

This framework also supports adding dependencies to a test. In the @Test annotation, assign the "depends" parameter to a list of strings of the names of the tests (e.g. @Test(depends="foo","bar")) that the test depends on. If any of the dependency tests fail, the test will not run, and you will be notified in the output that it did not run along with which of its dependencies that failed.
