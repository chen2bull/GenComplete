#!/usr/bin/env bash

# 这是一个路由函数,根据参数的不同路由到不同的函数
function __gen_comp_grun() {
    #$COMP_CWORD是系统自动变量，表示当前命令单词索引。 0是第一个单词(也就是mgectl)的索引号，。
    case $COMP_CWORD in
        0)#仍在完成根命令，这里不需要处理
        ;;
        1)  #根命令已经完成，这里开始补充一级主命令
            #${COMP_WORDS[0]}是根命令，在这个例子中就是mgectl
            eval __gen_comp_grun_main
        ;;
        *)  #一级主命令已经完成，这里开始补充一级主命令的选项
            #${COMP_WORDS[1]}是一级主命令，在这个例子中就是stop、debug或者manager
            local curarg=${COMP_WORDS[COMP_CWORD]}
            case "${COMP_WORDS[$COMP_CWORD - 1]}" in
                "-ps")
                    local __comp_all=$(ls *.class)
                    COMPREPLY=( $(compgen -W "${__comp_all}" -- "${curarg}") )
                    ;;
                "-encoding")
                    local __comp_all=""gb2312" "gb18030" "utf-8" "shift-jis""
                    COMPREPLY=( $(compgen -W "${__comp_all}" -- "${curarg}") )
                    ;;
                "some")
                    local __comp_all=$(ls -d */)
                    COMPREPLY=( $(compgen -W "${__comp_all}" -- "${curarg}") )
                    ;;
                *)
                    eval __gen_comp_grun_main
                    ;;
            esac
    esac
}

function __gen_comp_grun_main() {
    local curarg=${COMP_WORDS[COMP_CWORD]}
    local __comp_grun_file=$(ls)
    local __comp_grun_file_only=$(ls -la | awk '/^-/{print $NF}')
    local __comp_all=""${__comp_grun_file}" "${__comp_grun_file_only}" "-tokens" "-tree" "-gui" "-ps" "-trace" "-encoding" "some""
    COMPREPLY=( $(compgen -W "${__comp_all}" -- "${curarg}") )
}
complete -F __gen_comp_grun grun