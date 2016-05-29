grammar GenConfig;

file:commend_dec options_dec arrays;

commend_dec:'command' ID ';';

options_dec:'options' '{' (option_list';')+ '}' ';';

option_list: option+;
option:STRING|
    ARRAYNAME|
    ARRAYNAME_FILE|
    ARRAYNAME_FILEONLY|
    ARRAYNAME_DIR|
    ARRAYNAME_FILESUFF;

arrays:array_dec*;

array_dec:'array' ARRAYNAME '{' STRING(','STRING)*'}' ';';

ARRAYNAME_FILE:'$file';
ARRAYNAME_FILEONLY:'$file_only';
ARRAYNAME_DIR:'$dir';
ARRAYNAME_FILESUFF:'$file' '.' [a-zA-Z0-9]+;
ARRAYNAME:'$'[a-zA-Z0-9]+;

ID  :   [_a-zA-Z][_a-zA-Z0-9]* ;

STRING: '"'(ESC|.)*?'"';    // 这里ESC必须在点号之前,这样才能正常匹配"what\"else"
                            // 如果点号在ESC前面的话匹配到"what\"就会结束了

SPACE: (' ' | '\t' |'\r'?'\n') -> skip;

LINE_COMMENT : '//' .*? '\r'? '\n' -> skip ;
COMMENT      : '/*' .*? '*/' -> skip ;
fragment ESC: '\\"' | '\\\\';