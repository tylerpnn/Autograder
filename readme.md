My plans for this autograder:

I want to be able to use json to define the entire expected class structure and all the information about each class. Eventually I will also write a program that will automatically generate the json by parsing supplied java files. The Json will also contain information about individual tests. The tests will function the same way as Junit tests, but with point values and comments associated with them. The autograder will be supplied a json and a java class containing all of the tests, and then run all of the tests corresponding to the test names in the json using reflection, and if the test is failed it will deduct the associated points and add the test's comment to the list of comments.
