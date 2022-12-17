# Git

linux:常用命令

```
cd 改变目录
cd .. 回到上一个目录，直接cd进入默认目录
pwd 显示当前所在目录路径
ls(ll) 都是列出当前目录中所有文件，只不过ll列出的更详细
touch 新建一个文件如touch index.js 就会在当前目录下新建一个index.js文件
rm 删除一个文件
mkdir 新建文件夹
rm -r 删除文件夹 rm -r src 删除src目录
mv 移动文件, mv index.html src ，index是要移动的文件src是目标文件夹
reset 重新初始化终端/清屏
clear 清屏
history	查询操作历史
help 帮助
exit 退出
# 注释
```

工作原理：https://www.lsbin.com/9389.html

![96144eefd49108003a4c0f22596e110](C:\Users\10602\AppData\Local\Temp\WeChat Files\96144eefd49108003a4c0f22596e110.png)

![0a26e1fc379c2186319d08d643f6528](C:\Users\10602\AppData\Local\Temp\WeChat Files\0a26e1fc379c2186319d08d643f6528.png)



操作流程：

先 git init 初始化

1和2都是在本地操作

1. git add .
2. git commit -m "xxxx"
3. git push 推送到远程仓库用的



.gitignore 文件可以控制哪些文件不被git到仓库

```
*.txt 代表所有txt结尾文件
！lib.txt lib.txt文件除外
/temp 仅忽略根目录下的todo文件，但不包括其他目录temp （往上忽略）
build/ 忽略build/目录下的所有文件 （往下忽略）
doc/*.txt 会忽略doc/notes.txt 但不包括doc/server/arch.txt
```



链接github：

1. 在C:\Users\10602\ .ssh下开启github
2. 输入ssh-keygen -t rsa 生成公钥和密钥
3. 去github个人账户的setting目录下的SSH keys and GPG keys
4. new SSH key，然后吧生成的pub后缀文件内复制粘贴，最后的邮箱改一下
5. 创建仓库
6. 把新创建的仓库内的东西clone到本地(ssh克隆)

使用idea：

把clone下来的东西直接剪切到 idea的项目里就行了



branch命令：

```
git branch [branch-name] 创建一个分支
git checkout [branch-name] 切换分支
git checkout -b [branch-name] 创建并切换分支
git branch -d [branch-name] 删除分支

删除远程分支 
git push origin --delete [branch-name]
git branch -dr [remote/branch]
```



上传到其他branch：

1. 把东西pull到master上，然后本地创建一样的分支并切换过去
2. 合并master和新建的分支内容git merge master
3. 修改文件之后 git add .
4. git commit 
5. git push origin my-branch

最后push的时候使用的 git push origin my-branch  其中 my-branch是你想传到的branch名称

