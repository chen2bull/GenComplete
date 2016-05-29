#功能
    为某一条命令生成completion脚本

#使用方法
###1.在input_configs/目录下,添加一个配置文件,比如grun.genconf
###2.运行gen.sh
```
./gen.sh
```
###3.将output_scripts/目录下的脚本拷贝到某个目录
```
cp ./output_scripts/grun_completion.bash <目标目录>
```
###4.在.bashrc中,执行source(也可以拷贝到/etc/bash_completion.d/下,将自动生效)
```
source <目标目录>/*.bash
```

#默认提示数组
    $file           当前目录下的所有文件
    $file_only      当前目录下非目录的文件
    $dir            当前目录下的子目录
    $file.xxx       当前目录下扩展名为*.xxx的文件

