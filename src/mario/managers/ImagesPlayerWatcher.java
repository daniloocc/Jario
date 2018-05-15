package mario.managers;
// ImagesPlayerWatcher.java

/* When an ImagesPlayer gets to the end of a sequence, it can
   call sequenceEnded() in a listener.
*/

public interface ImagesPlayerWatcher 
{
    void sequenceEnded(String imageName);
}

