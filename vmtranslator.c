#include <stdio.h>
#include <string.h>

#define ONE_COMMAND_CHARS_BUFFER 50

const char* PUSH_COMMAND = "push";
const char* ARGUMENT_MEMORY_SEGMENT = "argument";
const char ARGUMENT_ADDRESS_LOCATION = '2';

main()
{
    parse("test.txt");
}

void parse(char* file_name)
{
    FILE* file;

    if (file_name != NULL)
    {
        file = fopen(file_name, "r");
    }
    else { return; }

    int c;
    char line_buffer[ONE_COMMAND_CHARS_BUFFER];
    char cursor;

    while ((c = getc(file)) != EOF)
    {
        if ('\n' == (char) c)
        {
            line_buffer[cursor++] = '\0';
            split_args(line_buffer);
            memset(line_buffer, '\0', cursor + 1);
            cursor = 0;
        }
        else
        {
            line_buffer[cursor++] = (char) c;
        }
    }

    fclose(file);
}

void split_args(char* command_line)
{
    char* command_token = strtok(command_line, " ");

    if (command_token != NULL)
    {
        if (strcmp(command_token, PUSH_COMMAND) == 0)
        {
            char* memory_segment = strtok(NULL, " ");
            char* offset = strtok(NULL, " ");

            handle_push_cmd(memory_segment, offset);
        }
    }
}

void handle_push_cmd(char* memory_register, char* offset)
{
    // This is not a ggreat idea because there are dynamic parts of the push command. For example, the offset calculation. Check how can we
    char* asm_command[100];



}
