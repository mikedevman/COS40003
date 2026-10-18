#include <stdio.h>
#include <unistd.h>
#include <sys/wait.h>

int main() {
    printf("Hello World\n");
    fflush(stdout);

    pid_t pid = fork(); // create a new process

    if (pid < 0) { 
        printf("Fork failed\n");
        return 1;
    }

    if (pid == 0) {
        // child process
        printf("Child process: PID = %d\n", getpid());
        fflush(stdout);
        execlp("whoami", "whoami", NULL); // replace the child process with the "whoami" command
    }
    else {
        printf("Parent process: PID = %d\n", getpid());
        fflush(stdout);
        wait(NULL); // wait for the child process to finish
        printf("Child process finished\n");
    }

    return 0;
}