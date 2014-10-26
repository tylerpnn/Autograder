This project is a autograding and testing framework for java programs, written in java. It is very similar to junits, however it uses json to outline comments and point values with the tests. The json can also define expected class structure, and in the future, the class structure of the program being tested can also be evaluated against the class structure outline in the json.

Currently the program can parse the json outlining the tests and class structure, run tests (it is *almost* completely backwards-compatible with junits tests, just change your imports), and parse a provided java class containing tests (i.e. a class written using junits tests) to generate a json template for all of the tests contained in the class.

Usage:

To parse a pre-written test class and generate the json template:

java -cp .:autograder.jar edu.gatech.cs1331.Parser [class_name] [output.json]

To run a test file with a provided json:

java -jar autograder.jar -t [class_name] [json_filename]