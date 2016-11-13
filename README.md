### LoudHailer – Reaching your audience!
A mnmlst voice recognition app based on [Akka Streams](http://akka.io), [wit.ai](https://wit.ai/) (aka NLP as service) and [Firebase](firebase.google.com) for messaging. 

![Vision](http://i.imgur.com/CvWgRl0l.png) 

For more information checkout my [blog posts](http://qabbasi.github.io/articles/2016-09/loudhailer-vision). 

### How to run the stream?
1. Create an account @ [wit.ai](https://wit.ai/) and copy the access token to [wit.token](https://github.com/qabbasi/Loudhailer/blob/master/voice-recognition-stream/src/main/resources/application.conf) 
2. Create an account @ [Firebase](http://firebase.google.com) and copy the access token to [firebase.token](https://github.com/qabbasi/Loudhailer/blob/master/voice-recognition-stream/src/main/resources/application.conf) 

### How to run the client app?
1. Create a new client project at [Firebase](http://firebase.google.com) (by now I assume you already have an account)
2. Just follow the instructions there and finally copy the `google-services.json` to [the root of the client directory.](https://github.com/qabbasi/Loudhailer/tree/master/LoudHailerClient) 
