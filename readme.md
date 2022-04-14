# Tree

## Description
This project is an analogue of the `tree` command for `UNIX` systems. `tree` is not an in-built command and should be installed. For `MacOS` users, it's easy to install with `brew`: `brew install tree`. For `Ubuntu`, use `apt-get install tree`.

### Program arguments
Similar to the `tree` command, this program support receiving multiple arguments and processes them correctly.

### Flags
Six flags are supported: `-L <n>`, `-a`, `-u`, `-g`, `-s`, `-p`. You can find out what each flag does either experimentally or by searching in Google.

Here are some examples of running a command with or without flags:
```
tree
tree -a -g -u
tree -L 2 -g -u
```

Note, if the `L` flag was passed, then the depth must be followed by the next argument (a number greater than zero).

Flags in arguments can be combined:
```
tree -ugpasL 2
tree -ug -asL 2 -p
```

In this case, if the `L` flag is found in some group of flags, it must also be followed by the value for the `L` argument.

Repetitions of flags are not prohibited. In this case, the last value will be selected for the `L` flag if it was repeated. For example, a command like this:

`tree -ag -Lua 2 -us -aL 5`

will be equivalent to this one:

`tree -a -g -L 5 -u -s`

Nothing depends on the order of the flags.

### Directory path argument
Tree can optionally accept the path to the directory where the command is to be executed. If you pass it, then tree will be launched from this directory, and not from the one in which the terminal is opened.

Print contents of the current directory:
`tree -L 2`
```
.
├── main.c
├── readme.md
├── setup
│   └── requirements.txt
├── test
│   ├── __pycache__
│   ├── gen_test_folder.py
│   └── test.py
└── venv
    ├── bin
    ├── include
    ├── lib
    └── pyvenv.cfg

7 directories, 6 files
```
Print contents of the directory `venv/bin`:
`tree /venv/bin -a`
```
venv/bin
├── Activate.ps1
├── activate
├── activate.csh
├── activate.fish
├── pip
├── pip3
├── pip3.9
├── python -> python3.9
├── python3 -> python3.9
└── python3.9 -> /usr/local/opt/python@3.9/bin/python3.9

0 directories, 10 files
```

Moreover, if you want to print tree from several directories at once, then you can do this simply by passing several paths to the command:
`tree venv/bin ~/desktop/caos-hw-tree -L 2`
```
venv/bin
├── Activate.ps1
├── activate
├── activate.csh
├── activate.fish
├── pip
├── pip3
├── pip3.9
├── python -> python3.9
├── python3 -> python3.9
└── python3.9 -> /usr/local/opt/python@3.9/bin/python3.9
/Users/costello1329/desktop/caos-hw-tree
├── main.c
├── readme.md
├── setup
│   └── requirements.txt
├── test
│   ├── __pycache__
│   ├── gen_test_folder.py
│   └── test.py
└── venv
    ├── bin
    ├── include
    ├── lib
    └── pyvenv.cfg

7 directories, 16 files
```
Directory arguments and flags can be mixed. Example:
`tree test/testdir_1 -au test/testdir_2 -ga -gsL 4`
In this case, the program recognizes that the supplied command is equivalent to this:
`tree test/testdir_1 test/testdir_2 -a -u -g -s -L 4`


## Copyright

![Creative Commons Licence](https://i.creativecommons.org/l/by-sa/4.0/88x31.png)

All materials are available under license [Creative Commons «Attribution-ShareAlike» 4.0](http://creativecommons.org/licenses/by-sa/4.0/).\
When borrowing any materials from this repository, you must leave a link to it, also, you should include my name: **Konstantin Leladze**.

__© Konstantin Leladze__

