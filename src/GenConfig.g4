grammar GenConfig;

file:commendDec optionsDec arrays;

commendDec:'command' ID ';';

optionsDec:'options' '{' (optionList';')+ '}' ';';

optionList: option+;
option:String|
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

String: '"'(Esc|.)*?'"';    // 这里ESC必须在点号之前,这样才能正常匹配"what\"else"
                            // 如果点号在ESC前面的话匹配到"what\"就会结束了

Space: (' ' | '\t' |'\r'?'\n') -> skip;

LineComment : '//' .*? '\r'? '\n' -> skip ;
Comment      : '/*' .*? '*/' -> skip ;
fragment Esc: '\\"' | '\\\\';