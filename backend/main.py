# Dummy Flask code obtained from https://medium.com/@Joachim8675309/jenkins-ci-pipeline-with-python-8bf1a0234ec3
from flask import Flask
app = Flask(__name__)

@app.route('/')
@app.route('/hello/')
def hello_world():
    return 'Hello World!\n'

@app.route('/hello/<username>') # dynamic route
def hello_user(username):
    return 'Why Hello %s!\n' % username

if __name__ == '__main__':
    app.run(host='0.0.0.0')     # open for everyone