class Greeter:
    def __init__(self, name):
        self.name = name
    
    def greet(self):
        print("Hello, " + self.name + "!")


greeter = Greeter("John")
greeter.greet()