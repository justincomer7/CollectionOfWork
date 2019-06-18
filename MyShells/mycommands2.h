#define DELIM " \t\r\n\a"
#define BUFF_FOR_GREP 100
#define BUFFSIZE 64
#include <grp.h>
#include <pwd.h>
#include <locale.h>
#include <time.h>
#include <langinfo.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <sys/types.h>

void welcome()
{
	printf("***************************************\n");
	printf("**Welcome to Justin and Dylan's Shell**\n");
	printf("***************************************\n\n");
}
char *read_line()
{
	
	char *line;
	line = (char *)malloc(BUFFSIZE * sizeof(char));
	
	size_t buffsize = BUFFSIZE;
	getline(&line, &buffsize, stdin);
	return line;
}

void env_print(char **envp)
{
	for(char **env = envp; *env!= 0; env++){
				char *thisEnv = *env;
				printf("%s\n", thisEnv);
	}
}

void time_out(int limit, char *command)
{
	pid_t pid;
	pid = fork();
	int status;
	if(pid == 0){
		alarm(limit);
		for(int i = 0; i < 1000000000000000000; i++)
		{
			i = i * i; 
		}
		alarm(0);
		exit(0);
	}
	else
		wait(&status);
}

void make_dir(char *name)
{
	struct stat st = {0};
		  
			if (stat(name, &st) == -1) {
				if((mkdir(name, S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH) != 0))
					printf("error making directory\n");
			}
			else
				printf("mkdir: cannot create directory '%s': File exists\n", name);
}

void change_dir(char *name)
{
	struct stat st = {0};
		  
			if (stat(name, &st) == -1) {
				printf("-bash: cd: %s: No such file or directory\n", name);
			}
			else
				if(chdir(name) != 0)
					printf("Error changing directory\n");
}

void remove_dir(char *name)
{
	struct stat st = {0};
		  
			if (stat(name, &st) == -1) {
				printf("rm: cannot remove '%s': No such file or directory\n", name);
			}
			else
				if(rmdir(name) != 0)
					printf("Error removing directory\n");
}

void get_stat(char *name)
{
	char buf[BUFFSIZE];
	struct stat mystat;
	sprintf(buf, "%s", name);
	if(stat(buf, &mystat) != 0)
		printf("stat: cannot stat '%s': No such file or directory", name);
	else{
		printf("  File: '%s'\n",name); //file name 
		printf("  Size: %zu	", mystat.st_size); //file size 
		printf("Blocks: %zu	", mystat.st_blocks); //which block 
		printf("IO Block: %zu	", mystat.st_blksize); //block size 
		printf(S_ISREG(mystat.st_mode) ? "regular file\n" : "directory\n"); //type of file 
		printf("Device: %zu  ", mystat.st_dev); //device the the file is on 
		printf("Inode: %zu  ", mystat.st_ino); // inode value 
		printf("Links: %zu\n", mystat.st_nlink); //number of hard links 
		
		//values used to display permissions in octal 

		int m = 0;
		int l = 0;
		int k = 0;
		
		((mystat.st_mode & S_IRUSR) ? (m = m + 4) : (m = m));
		((mystat.st_mode & S_IWUSR) ? (m = m + 2) : (m = m));
		((mystat.st_mode & S_IXUSR) ? (m = m + 1) : (m = m));
		((mystat.st_mode & S_IRGRP) ? (l = l + 4) : (l = l));
		((mystat.st_mode & S_IWGRP) ? (l = l + 2) : (l = l));
		((mystat.st_mode & S_IXGRP) ? (l = l + 1) : (l = l));
		((mystat.st_mode & S_IROTH) ? (k = k + 4) : (k = k));	
		((mystat.st_mode & S_IWOTH) ? (k = k + 2) : (k = k));
		((mystat.st_mode & S_IXOTH) ? (k = k + 1) : (k = k));
		printf("Access: (0%i%i%i/", m,l,k); // file permissions in octal 
		
		printf((S_ISDIR(mystat.st_mode)) ? "d" : "-");
		printf((mystat.st_mode & S_IRUSR) ? "r" : "-");
		printf((mystat.st_mode & S_IWUSR) ? "w" : "-");
		printf((mystat.st_mode & S_IXUSR) ? "x" : "-");
		printf((mystat.st_mode & S_IRGRP) ? "r" : "-");
		printf((mystat.st_mode & S_IWGRP) ? "w" : "-");
		printf((mystat.st_mode & S_IXGRP) ? "x" : "-");
		printf((mystat.st_mode & S_IROTH) ? "r" : "-");	
		printf((mystat.st_mode & S_IWOTH) ? "w" : "-");
		printf((mystat.st_mode & S_IXOTH) ? "x)	 " : "-)  "); //file permissions 
		
		struct passwd *ow = getpwuid(mystat.st_uid); // user id 
		if(ow != NULL){
			printf("Uid: ( %u/", mystat.st_uid);
			printf("%-12.12s)	", ow->pw_name);
		}
		
		struct group *grp = getgrgid(mystat.st_gid); // group id 
		if(grp != NULL){
			printf("Gid: ( %u/", mystat.st_gid);
			printf("%-12.12s)\n", grp->gr_name);
		}
		printf("Access: %s", ctime(&mystat.st_atime)); //access time 
		printf("Modify: %s", ctime(&mystat.st_mtime)); //modification time 
		printf("Change: %s", ctime(&mystat.st_ctime)); //creation time 
		
	}
}

void get_Pid(){
	printf("%d\n", getpid());
}

char **parser(char *line)
{
	int position = 0;
	char **tokens = malloc(BUFFSIZE * sizeof(char*));
	char *token;

	if(!tokens)
	{
		fprintf(stderr, "allocation error\n");
		exit(0);
	}
	token = strtok(line, DELIM);
	while (token != NULL)
	{
		tokens[position] = token;
		position++;

		token = strtok(NULL, DELIM);
	}

	tokens[position] = NULL;
	return tokens;
}
