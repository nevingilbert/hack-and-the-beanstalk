#Beanstalk
### Inspiration
Group study is very common and when conversations break out, people find their discussion drowned out by the music. Even after fumbling with the volume controls, it is difficult to find and maintain a comfortable volume for everyone.

### What it does
Enter Beanstalk, which promotes active conversation between our users by detecting speech in the environment and adjusting a managed Bose speaker's volume so that nobody is shouting over the music. When the conversation is over, Beanstalk seamlessly takes you back to your music. 

### How I built it
Beanstalk is an Android application developed in Android Studio in which the user can activate adaptive audio control once they are satisfied with the overall sound of the music and any conversation in the room. Adaptive audio control will then adjust the music volume to maintain the noise level of the room based on speech and the Bose Sound-Touch API. 

### Challenges I ran into
We ran into a few large challenges while developing our Android app. 

One of the main issues when working with sound and volume technology, is that the most common units of measurement is decibels. Decibels aren't easy to work with since they are a logarithmic based unit of measurement. We had to add extra logic and calculations to our design in order to work around these issues.

### Accomplishments that I'm proud of
We are proud of developing a working, mostly complete, prototype in such a short period of time. In addition, we created a frontend design that we are satisfied with, and enhances the usability of our project.

### What I learned
We learned a lot about Android development, usage of APIs that work over local networks, and some of the mathematics involved when making calculations about volume and decibels.

### What's next for Beanstalk
We want to eventually develop Beanstalk into a full suite of technologies that allow users to leverage the power of the Bose Sound-Touch APIs. We had planned additional features, that would both enhance our current capabilities and add new features, but we were unable to implement these due to the time constraints of the hackathon.