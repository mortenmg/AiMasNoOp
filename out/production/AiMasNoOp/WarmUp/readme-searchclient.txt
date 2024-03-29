/*******************************************************\
|                AI and MAS: SearchClient               |
|                        README                         |
\*******************************************************/

    This readme describes how to use the included SearchClient with the Hospital server that is contained in server.jar.

    Starting the server using the SearchClient solver
        $ java -jar server.jar -l SAD1.lvl -g 50 -c "java searchclient.SearchClient"
    
    Read more about the server options using the -? argument:
        $ java -jar server.jar -?
    You can also have a look at the readme-server.txt, although see if you can't get by without it.
    
    Compiling SearchClient from the directory of this readme:
        $ javac searchclient/*.java
    
    ai.Memory settings:
        * Unless your hardware is unable to support this, you should let SearchClient allocate at least 1GB of memory *
        Your JVM determines how much memory a program is allowed to allocate. These settings can be manipulated by certain VM options.
        The -Xmx option sets the limit for how much memory a process is allowed to consume and is most interesting option here.
        To set the max heap size to 2GB: 
            $ java -jar server.jar -l SAD1.lvl -g 50 -c "java -Xmx2048m searchclient.SearchClient"
            $ java -jar server.jar -l SAD1.lvl -g 50 -c "java -Xmx2g searchclient.SearchClient"
        Note that this option is set in the *client* (and not the server)
        Avoid setting max heap size to high, since it will lead to your OS doing memory swapping which is terribly slow.
        A primer on the workings of memory in JVM can be found at: http://www.avricot.com/blog/?post/2010/05/03/Get-started-with-java-JVM-memory-(heap%2C-stack%2C-xss-xms-xmx-xmn...)

    Low framerates when rendering:
        We experienced poor performance when rendering on Linux. The reason was that hardware rendering was not turned on. 
        To enable OpenGL hardware acceleration you should use the following option: -Dsun.java2d.opengl=true
        Note that this VM option must be set in the Java command that invokes the *server* (and not SearchClient):
            $ java -Dsun.java2d.opengl=true -jar server.jar -l SAD1.lvl -g 50 -c "java searchclient.SearchClient"
        See http://docs.oracle.com/javase/6/docs/technotes/guides/2d/flags.html for more information

    Eclipse:
        You're of course welcome to use an IDE (e.g. Eclipse) for this assignment.
        To set command line arguments in Eclipse:
            - Program arguments (e.g. -l, -g, -c) are set in Run Configuration > Arguments > Program arguments
            - VM arguments *for the server* (e.g. -Dsun.java2d.open=true) are set in Run Configurations > Arguments > VM Arguments
