#include <sys/wait.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <dirent.h>
#include <sys/stat.h>
#include "mycommands.h"




int main() {
  char * line = (char * ) malloc(BUFFSIZE * sizeof(char));
  int status = 0;
  char * exit = (char * ) malloc(4 * sizeof(char));

  char * li = (char * ) malloc(2 * sizeof(char));
  char * la = (char * ) malloc(2 * sizeof(char));
  char * co = (char * ) malloc(2 * sizeof(char));
  char * ca = (char * ) malloc(3 * sizeof(char));
  char * cl = (char * ) malloc(5 * sizeof(char));
  char * gp = (char * ) malloc(4 * sizeof(char));

  char * * commands = malloc(BUFFSIZE * 2 * sizeof(char * ));
  strcpy(exit, "exit");
  	commands[0] = exit;
  strcpy(li, "ls");
  	commands[1] = li;
  strcpy(la, "-l");
  strcpy(co, "cp");
  	commands[2] = co;
  strcpy(ca, "cat");
  	commands[3] = ca;
  strcpy(cl, "clear");
  	commands[4] = cl;
  strcpy(gp, "grep");
  	commands[5] = gp;

  int size = sizeof(commands) / sizeof(commands[0]);
  welcome();

  int z; //variable z will be used for strcomp in our cat function

  do {
    printf(">");
    line = read_line();

    char * * parsed = malloc(BUFFSIZE * sizeof(char * ));

    parsed = parser(line);

    int i, found = 0;
    for (int l = 0; l < 6; l++) {
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
        return 0;
        break;
        /*ls*/
      case (1):
        {
          if (parsed[1] == NULL) {
            list_files();
          }
          /*ls -l*/
          else if (strcmp(parsed[1], la) == 0) {
            list_long();
          } else
            printf("List command not recognized\n");
        }
        break;
        /*cp*/
      case (2):
        copy_file(parsed[1], parsed[2]);
        break;
        /*cat*/
      case (3):
        {
          /*cat <blank>*/
          if (parsed[1] == NULL) {
            printf("Please try again. Proper format: cat fileName.optionalFileExtension\n");
          }
          /*cat file1*/
          if (parsed[1] != NULL) {
            if (parsed[2] == NULL) {
              print_file(parsed[1]);
            }
            /*cat file1 file2*/
            if (parsed[2] != NULL && parsed[3] == NULL) {
              print_file(parsed[1]);
              print_file(parsed[2]);
            }
            /*cat file1 (file2||>) (file3||file2)*/
            if (parsed[2] != NULL && parsed[3] != NULL) {
              z = strcmp(parsed[2], ">");
              if (z == 0) {
                copy_file(parsed[1], parsed[3]);
              } else if (z != 0) {
                print_file(parsed[1]);
                print_file(parsed[2]);
                print_file(parsed[3]);
              }
              //if(strcmp(parsed[2],">") != 0)
              //	printf("Error\n");
              //else
              //copy_file(parsed[1], parsed[3]);

            }
          }
          break;
        case (4):
            printf("\033[H\033[J");
          break;
        case (5):
          {
		int size = get_size(parsed);; //used to count the size of parsed
		int t = 2; //used to track which file is going in if i > 3
		

		if(size > 3){
			while (t < size){
			find_mul(parsed[t], parsed[1]); 
			t++;
			}
		}
		else
            		find(parsed[2], parsed[1]);

          }
          break;
        }
      }
  } while (status != 1);
}
