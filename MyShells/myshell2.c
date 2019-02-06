#include <sys/wait.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <dirent.h>
#include <sys/stat.h>

#include "mycommands2.h"

 char * * commands; // creates global variables 
 char **env;

int get_size(char ** array){ // get size of array of strings 
	int size = 0;
	while(array[size] != NULL)
			size++;
		
	return size;
}

int check_digit(char *number){ //will ensure the command is a number 
	char check[100];
	strcpy(check, number);
	
	for (int i = 0; i < strlen(check); i++){
		if( check[i] >= '0' && check[i] <= '9' ){
			i++;
		}
		else 
			return 0;
	}
	
	return 1;
}

static void sig_alrm(int signo)
{
	//handles signal
}

void kill_1(char * p){
	int pid = atoi(p);
	if(pid == getpid())
		exit(1);
	else
		printf ("- bash: kill:(%s) - No such process\n", p);
}

void run_commands (char **parsed){ 
    int i, found = 0;
	
    for (int l = 0; l < get_size(commands); l++) {
      if (strcmp(parsed[0], commands[l]) == 0) {
        found = 1;
        i = l;
        break;
      }
    }

    if (found == 0) {
      printf("%s: command not found\n", parsed[0]);
    } else
      switch (i) {

        /*exit*/
      case (0):
        exit(0);
        break;
        /*env*/
      case (1):
        {
          if (parsed[1] == NULL) {
            env_print(env);
			}
			
			else
				printf("env: %s: No such file or directory\n", parsed[1]);
        }
        break;
	  case (2): //timeout
		{
			printf("timeout repeated\n");
		}
	  case (3): //mkdir
	    {
		  make_dir(parsed[1]);
	    }
		break;
	  case (4) : //cd
		{
			change_dir(parsed[1]);
		}
		break;		
	  case (5) : //sleep
		{	
		  if(check_digit(parsed[1])){ // make sure its a number
			if(parsed[2] != NULL)
				for(int sel = 2; sel < get_size(parsed); sel++)
						printf("sleep: invalid time interval '%s'\n", parsed[sel]);
			else{
				char num[100];
				strcpy(num, parsed[1]);
				int len = strlen(num);
				int dec = 0, i;

				for (i = 0; i<len; i++){
					dec = dec * 10 + (num[i] - '0'); // makes the second string an int to be used for alarm
				}
				if(signal(SIGALRM, sig_alrm) == SIG_ERR)
					printf("error in signals\n");
				
				int length = 0;
				
				char com[100];
				alarm(dec);
				pause();
				alarm(0);
			}
		  }
		  else
			  printf("sleep: invalid time interval '%s'\n", parsed [1]);  //no time given
		  
		}
	  break;
	  case (6) : //rmdir 
	  {
		  remove_dir(parsed[1]);
	  }
	  break;
	  case (7) : //stat
	  {
		  if(parsed[1] == NULL)
		  {
			  printf("stat: missing operand\n");
		  }
		  else{
			  int i = 1;
			  while(parsed[i] != NULL){
				get_stat(parsed[i]);
				i++;
			  }
		  }
	  }
	  break;
	  case (8) : //gets Pid for kill command 
	  {
		  get_Pid();
	  }
	  break;
	  case (9) : //calls kill 
	  {
		  if(parsed[1] != NULL)
			kill_1(parsed[1]);
		  else	
			  printf("Need process number\n");
	  }
	  break;
	}
}



int main(int argc, char **argv, char **envp) {
	env = envp;
	char * line = (char * ) malloc(BUFFSIZE * sizeof(char));
  int status = 0;
  char * exit = (char * ) malloc(4 * sizeof(char));
  char * en = (char * ) malloc(2 * sizeof(char));
  char * ti = (char * ) malloc(7 * sizeof(char));
  char * mk = (char * ) malloc(5 * sizeof(char));
  char * cd = (char * ) malloc(2 * sizeof(char));
  char * sl = (char * ) malloc(5 * sizeof(char));
  char * rm = (char * ) malloc(5 * sizeof(char));
  char * st = (char * ) malloc(4 * sizeof(char));
  char * pi = (char * ) malloc(5 * sizeof(char));
  char * ki = (char * ) malloc(4 * sizeof(char));
  
  commands = malloc(BUFFSIZE * 2 * sizeof(char * ));

 
  strcpy(exit, "exit");
  	commands[0] = exit;
  strcpy(en, "env");
  	commands[1] = en;
  strcpy(ti, "timeout");
	commands[2] = ti;
  strcpy(mk, "mkdir");
	commands[3] = mk;
  strcpy(cd, "cd");
	commands[4] = cd;
  strcpy(sl, "sleep");
	commands[5] = sl;
  strcpy(rm, "rmdir");
	commands[6] = rm;
  strcpy(st, "stat");
	commands[7] = st;
  strcpy(pi, "getPid");
	commands[8] = pi;
  strcpy(ki, "kill");
	commands[9] = ki;
	
  
  
  welcome();

  int z; //variable z will be used for strcomp in our cat function

  do {
	char cwd[1024];
	if (getcwd(cwd, sizeof(cwd)) != NULL)
       fprintf(stdout, "~%s", cwd);
    printf("$ ");
    line = read_line();

    char * * parsed = malloc(BUFFSIZE * sizeof(char * ));

    parsed = parser(line);
	
	//checks to see if timeout was added 
	if(strcmp(parsed[0], ti) == 0)
	{
		if(parsed[2] == NULL)
			printf("Need Command\n");
		else{
			if(check_digit(parsed[1])){ // make sure its a number
				char num[100];
				strcpy(num, parsed[1]);
				int len = strlen(num);
				int dec = 0, i;
				char * * parsed2 = malloc(BUFFSIZE * sizeof(char * )); //going to contain commands after timout
				
				for (i = 0; i<len; i++){
					dec = dec * 10 + (num[i] - '0'); // makes the second string an int to be used for alarm
				}
				
				for(int l = 2; l < get_size(parsed); l++)
				{
					parsed2[l-2] = parsed[l];
				}
				
				pid_t pid;
				pid = fork(); //creates another process where alarm is ran
				int stat;
				if(pid == 0){
					alarm(dec);
					run_commands(parsed2);
					alarm(0);
					_exit(0);
				}
				
			else
				wait(&stat); // will wait for child to exit as to not create a zombie 
			}
			else 
				printf("timeout: invalid time interval '%s'\n", parsed [1]); 
		}
	}
	else
		run_commands(parsed); // if no timeout was added
}while( status != 1);
  
}