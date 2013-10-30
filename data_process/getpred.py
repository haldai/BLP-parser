#encoding=utf-8
import sys,re

if len(sys.argv) != 2:
    print 'input data path'
    exit(0)

data = sys.argv[1]
outpath = data.replace('dep', 'pred')
fin = open(data, 'r')
fout = open(outpath, 'w')

pred_list = []
pars = re.compile("[^\(]*\(([^\)]*)\).*")

while True:
    line = fin.readline()
    if not line:
        break
    line = line.strip()[0:-1]
    comps = line.split(':-')
    if len(comps) > 1:
        sent = comps[-1]
        print 'train'
    else:
        sent = comps[0]
        print 'test'
    terms = sent.split(';')
    for term in terms:
        print term
        p = term.split('(')[0].lower()
        print p
        if (p not in pred_list) and (len(p) > 1):
            pred_list.append(p)
            
fin.close()
for s in pred_list:
    fout.write('%s/2\n' % s)
fout.close()
