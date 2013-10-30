import sys, os

if len(sys.argv) < 2:
    print "not enough arguments"

input_path =  sys.argv[1]
fin = open(input_path, 'r')
flabel = open(input_path.replace('txt', 'label'), 'w')
fsent = open(input_path.replace('txt', 'sentence'), 'w')

while True:
    line = fin.readline()
    if not line:
        break
    line = line.strip()
    [head, body] = line.split(':-')
    flabel.write('%s\n' % head)
    fsent.write('%s\n'% body)
fin.close()
flabel.close()
fsent.close()
