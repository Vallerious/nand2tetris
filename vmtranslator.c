#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define ONE_COMMAND_CHARS_BUFFER 50
typedef char* string;

const string PUSH_COMMAND = "push";
const string POP_COMMAND = "pop";
const string ADD_COMMAND = "add";
const string SUB_COMMAND = "sub";
const string NEG_COMMAND = "neg";
const string AND_COMMAND = "and";
const string OR_COMMAND = "or";
const string NOT_COMMAND = "not";
const string EQ_COMMAND = "eq";
const string GT_COMMAND = "gt";
const string LT_COMMAND = "lt";
const string ARGUMENT_MEMORY_SEGMENT = "argument";
const char ARGUMENT_ADDRESS_LOCATION = '2';

int counter = 0;

string handle_push_cmd(string memory_register, string offset);
string handle_const_cmd(string offset);
string handle_pop_cmd(string memory_register, string offset);
string handle_add_cmd();
string handle_sub_cmd();
string handle_neg_cmd();
string handle_and_cmd();
string handle_or_cmd();
string handle_not_cmd();
string handle_eq_cmd(char offset);
string handle_gt_cmd(char offset);
string handle_lt_cmd(char offset);

main(int argn, char** args)
{
    FILE* input_file = fopen("StackTest.vm", "r");
    remove("output.asm");
    FILE* output_file = fopen("output.asm", "a+");

    parse(input_file, output_file);

    fclose(input_file);
    fclose(output_file);
}

void parse(FILE* input_file, FILE* output_file)
{
    int c;
    char line_buffer[ONE_COMMAND_CHARS_BUFFER];
    char cursor;

    while ((c = getc(input_file)) != EOF)
    {
        if ('\n' == (char) c)
        {
            line_buffer[cursor++] = '\0';
            handle_cmd(line_buffer, output_file);
            memset(line_buffer, '\0', cursor + 1);
            cursor = 0;
        }
        else
        {
            line_buffer[cursor++] = (char) c;
        }
    }
}

void handle_cmd(string command_line, FILE* output_file)
{
    string command_token = strtok(command_line, " ");

    if (command_token != NULL)
    {
        string asm_output;
        if (strcmp(command_token, PUSH_COMMAND) == 0)
        {
            string memory_segment = strtok(NULL, " ");
            string offset = strtok(NULL, " ");

            if (strcmp(memory_segment, "constant") == 0)
            {
                asm_output = handle_const_cmd(offset);
            }
            else
            {
                asm_output = handle_push_cmd(memory_segment, offset);
            }
        }
        else if (strcmp(command_token, POP_COMMAND) == 0)
        {
            string memory_segment = strtok(NULL, " ");
            string offset = strtok(NULL, " ");
            asm_output = handle_pop_cmd(memory_segment, offset);
        }
        else if (strcmp(command_token, ADD_COMMAND) == 0)
        {
            asm_output = handle_add_cmd();
        }
        else if (strcmp(command_token, SUB_COMMAND) == 0)
        {
            asm_output = handle_sub_cmd();
        }
        else if (strcmp(command_token, NEG_COMMAND) == 0)
        {
            asm_output = handle_neg_cmd();
        }
        else if (strcmp(command_token, AND_COMMAND) == 0)
        {
            asm_output = handle_and_cmd();
        }
        else if (strcmp(command_token, OR_COMMAND) == 0)
        {
            asm_output = handle_or_cmd();
        }
        else if (strcmp(command_token, NOT_COMMAND) == 0)
        {
            asm_output = handle_not_cmd();
        }
        else if (strcmp(command_token, EQ_COMMAND) == 0)
        {
            asm_output = handle_eq_cmd('0' + counter);
            counter++;
        }
        else if (strcmp(command_token, GT_COMMAND) == 0)
        {
            asm_output = handle_gt_cmd('0' + counter);
            counter++;
        }
        else if (strcmp(command_token, LT_COMMAND) == 0)
        {
            asm_output = handle_lt_cmd('0' + counter);
            counter++;
        }

        int result = fputs(asm_output, output_file);

        if (result == EOF)
        {
            return 2;
        }

        free(asm_output);
    }
}

string handle_push_cmd(string memory_register, string offset)
{
    string asm_command = malloc(32);
    strcpy(asm_command, "@k\nA=M\nD=M\n@0\nM=M+1\nA=M-1\nM=D\n");
    if (strcmp(memory_register, "local") == 0) { asm_command[1] = *offset; }
    return asm_command;
}

string handle_pop_cmd(string memory_segment, string offset)
{
    string asmCmd = malloc(28);
    strcpy(asmCmd, "@0\nM=M-1\nA=M\nD=M\n@k\nA=M\nM=D\n");
    if (strcmp(memory_segment, "local") == 0) { asmCmd[18] = *offset; }
    return asmCmd;
}

string handle_add_cmd()
{
    string asm_command = malloc(37);
    strcpy(asm_command, "@0\nM=M-1\nA=M\nD=M\nM=0\nA=A-1\nD=D+M\nM=D\n");
    return asm_command;
}

string handle_sub_cmd()
{
    string asm_command = malloc(37);
    strcpy(asm_command, "@0\nM=M-1\nA=M\nD=M\nM=0\nA=A-1\nD=M-D\nM=D\n");
    return asm_command;
}

string handle_neg_cmd()
{
    string asm_command = malloc(20);
    strcpy(asm_command, "@0\nD=A\nA=M-1\nM=D-M\n");
    return asm_command;
}

string handle_and_cmd()
{
    string asm_command = malloc(42);
    strcpy(asm_command, "@0\nM=M-1\nA=M\nD=M\nM=0\nA=A-1\nD=D&M\nM=D\n");
    return asm_command;
}

string handle_or_cmd()
{
    string asm_command = malloc(42);
    strcpy(asm_command, "@0\nM=M-1\nA=M\nD=M\nM=0\nA=A-1\nD=D|M\nM=D\n");
    return asm_command;
}

string handle_not_cmd()
{
    string asm_command = malloc(14);
    strcpy(asm_command, "@0\nA=M-1\nM=!M\n");
    return asm_command;
}

string handle_eq_cmd(char offset)
{
    string asm_command = malloc(110);
    strcpy(asm_command, "@0\nM=M-1\nA=M\nD=M\nM=0\nA=A-1\nD=D-M\n@EQ.cmp.x\nD;JEQ\n@0\nA=M-1\nM=0\n@END.x\n0;JMP\n(EQ.cmp.x)\n@0\nA=M-1\nM=-1\n(END.x)\n");
    asm_command[41] = asm_command[67] = asm_command[83] = asm_command[105] = offset;
    return asm_command;
}

string handle_gt_cmd(char offset)
{
    string asm_command = malloc(110);
    strcpy(asm_command, "@0\nM=M-1\nA=M\nD=M\nM=0\nA=A-1\nD=D-M\n@GT.cmp.x\nD;JLT\n@0\nA=M-1\nM=0\n@END.x\n0;JMP\n(GT.cmp.x)\n@0\nA=M-1\nM=-1\n(END.x)\n");
    asm_command[41] = asm_command[67] = asm_command[83] = asm_command[105] = offset;
    return asm_command;
}

string handle_lt_cmd(char offset)
{
    string asm_command = malloc(110);
    strcpy(asm_command, "@0\nM=M-1\nA=M\nD=M\nM=0\nA=A-1\nD=D-M\n@LT.cmp.x\nD;JGT\n@0\nA=M-1\nM=0\n@END.x\n0;JMP\n(LT.cmp.x)\n@0\nA=M-1\nM=-1\n(END.x)\n");
    asm_command[41] = asm_command[67] = asm_command[83] = asm_command[105] = offset;
    return asm_command;
}

string handle_const_cmd(string offset)
{
    string asm_command = malloc(40);
    strcpy(asm_command, "@");
    strcat(asm_command, offset);
    strcat(asm_command, "\nD=A\n@0\nM=M+1\nA=M-1\nM=D\n");
    return asm_command;
}
