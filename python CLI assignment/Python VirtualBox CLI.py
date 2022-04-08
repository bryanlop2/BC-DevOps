import os
import virtualbox

vbox = virtualbox.VirtualBox()
print('Wellcome! \n')

action = input('Wich action you want to use?')

#Start the Virtual Machine "Fedora_default_1649104257165_25684"
def startMachine():
    session = virtualbox.Session()
    machine = vbox.find_machine("Fedora_default_1649104257165_25684")
    progress = machine.launch_vm_process(session, "gui", [])
    progress.wait_for_completion()
    print(f'Machine running successfully')

#Create the Virtual Machine "OracleLinux6Test"
def createMachine():
    virtualMachine = vbox.create_machine(name="OracleLinux6Test",
                             os_type_id="Oracle_64",
                             settings_file="",
                             groups=['/'],
                             flags=""
                             )
    vbox.register_machine(virtualMachine)
    print(f'Machine successfully created')

#Delete the Virtual Machine "OracleLinux6Test"
def deleteMachine():
    virtualMachine = vbox.find_machine("OracleLinux6Test")
    virtualMachine.remove()
    print(f'Machine successfully deleted')

#To start the virtual Machine: start
#To create a new Virtual Machine: create
#To delete the Virtual Machine: delete
if action != 'exit':
    if(action == 'start'):
        startMachine()
    elif(action == 'create'):
        createMachine()
    elif(action == 'delete'):
        deleteMachine()
    else:
        print('that command was not found')
print('Good bye')


    

