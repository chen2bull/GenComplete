grammar GenConfig;

file:commendDec optionsDec arrays;

commendDec:'command' ID ';';

optionsDec:'options' '{' (optionList';')+ '}' ';';

optionList
    : String arrayName      #optionStringAndArray
    | String                #optionSingle
    | arrayName             #optionArray
    ;

arrayName:
    Arrayname|
    ArraynameFile|
    ArraynameFileOnly|
    ArraynameDir|
    ArraynameFileSuff;

arrays:arrayDec*;

arrayDec:'array' Arrayname '{' String(','String)*'}' ';';

ArraynameFile:'$file';
ArraynameFileOnly:'$file_only';
ArraynameDir:'$dir';
ArraynameFileSuff:'$file' '.' [a-zA-Z0-9]+;
Arrayname:'$'[a-zA-Z0-9]+;

ID  :   [_a-zA-Z][_a-zA-Z0-9]* ;

String: '"'(~[ \t\n])*?'"';    // 命令行参数中,单个参数中不允许有空格

Space: (' ' | '\t' |'\r'?'\n') -> skip;

LineComment : '//' .*? '\r'? '\n' -> skip ;
Comment      : '/*' .*? '*/' -> skip ;