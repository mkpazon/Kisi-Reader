# Kisi Reader

The Kisi Reader app demonstrates handling of NFC communication between two Android devices through the use of Android Beam. It works together with the Kisi Tag app.


### Demo
[Youtube Link](https://youtu.be/iRLu9utQvy0)

1. Both Kisi Tag and Kisi Reader are launched in different devices. Kisi Reader shows that it is currently locked. The Kisi Tag shows what payload is about to send.
2. The 2 devices are brought back to back to initiate Android Beam.
3. Tapping the screen while Android Beam has been initiated will send the payload from Kisi Tag to Kisi Reader
4. Kisi Reader processes the payload depending on its content

  | Payload | Behaviour    |
  |---------|--------------|
  | nothing | Does nothing |
  | unlock  | 1. sends an HTTP POST to `https://api.getkisi.com/locks/5124/access` <br>2. Upon successful HTTP response app redirects to main app screen
