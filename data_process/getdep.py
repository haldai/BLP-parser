#encoding=utf-8
import sys

if len(sys.argv) != 3:
    print 'input data path (origin + parsed)!'
    exit(1)

label = sys.argv[1]
if label != 'dummy':
    flabel = open(label, 'r')
parsed = sys.argv[2]
outpath = parsed.replace('parse' , 'dep')
word_seg = parsed.replace('parse', 'word_seg')

fpar = open(parsed, 'r')
fout = open(outpath, 'w')
fwordseg = open(word_seg, 'w')

dep_list = []
word_list_sents = []
while True:
    line = fpar.readline()
    if not line:
        break
    line = line.strip()
    if len(line) < 1:
        continue
    '''
    if line.find('Ner result') != -1:
        seg = ''
        word_list = []
        word_ner = line.split('=')[1].split('  ')
        for i in range(len(word_ner)):
            word = word_ner[i]
            cur = word.split(' ')[0]
            cur = cur.replace(',', '，')
            cur = cur.replace('?', '？')
            cur = cur.replace('(', '（')
            cur = cur.replace(')', '）')
            if cur not in ',?"，。？“”；;‘’、`':
                seg = seg + '%s ' % cur
            word_list.append(cur + '_%s_' % (i+1) + word.split(' ')[1])
        seg = seg.strip()
        fwordseg.write('%s\n' % seg)
    '''
    if line.find('query') != -1:
        # build word list
        tmp = ''
        dep_str = line.split(':')[1]
        deps = dep_str.split(';')
        word_list = []
        for i in range(len(deps) - 1):
            tmp_name = (deps[i].split('(')[0])
            tmp_name = tmp_name.replace('，', 'com')
            tmp_name = tmp_name.replace('？', 'qr')
            tmp_name = tmp_name.replace('（', 'lpa')
            tmp_name = tmp_name.replace('）', 'rpa')
            tmp_name = tmp_name + '_%d_' % (i + 1)
            tmp_name = tmp_name + deps[i].split('(')[1].split(' ')[0]
            word_list.append(tmp_name)
        word_list_sents.append(word_list)
        for i in range(len(deps) - 1):
            dep = deps[i]
            comp = dep.split(' ')
            name = comp[-1][0:len(comp[-1]) - 1]
            if (name == 'HED'):
                continue
            cur = word_list[i].split('_')[0]
            father = word_list[int(comp[-2])-1]
            tmp = tmp + ';%s(%s,%s)' % (name, father, word_list[i])
        dep_list.append(tmp[1:])

print len(dep_list)
fpar.close()
fwordseg.close()
cnt = 0
if label != 'dummy':
    while True:
        line = flabel.readline()
        if not line:
            break
        l = word_list_sents[cnt]
        line = line.strip()
        terms = line.split(';')
        tmp_list = []
        for i in range(len(terms)):
            term = terms[i]
            tmp_str = term[1:-1]
            tmp_wrds = tmp_str.split(',')
            for j in range(len(tmp_wrds)):
                word = tmp_wrds[j]
                if not word in tmp_list:
                    tmp_list.append(word)
                    print word
        print len(tmp_list)
        tmp_sub_list = []
        for i in range(len(tmp_list)):
            for j in range(len(l)):
                if l[j].split('_')[0] == tmp_list[i]:
                    tmp_sub_list.append(l[j])
                    print l[j]
                    break
        print len(tmp_sub_list)
        new_label = ''
        for i in range(len(terms)):
            term = terms[i]
            new_term = ''
            tmp_str = term[1:-1]
            tmp_wrds = tmp_str.split(',')
            for j in range(len(tmp_wrds)):
                word = tmp_wrds[j]
                pos = tmp_list.index(word)
                new_term = new_term + tmp_sub_list[pos] + ','
            new_term = '(' + new_term[0:-1] + ')'
            new_label = new_label + new_term + ';'
        fout.write('%s:-%s.\n' % (new_label[0:-1], dep_list[cnt]))
        cnt = cnt+1
    flabel.close()
else:
    for dep in dep_list:
        fout.write('(S,P):-%s.\n' % dep)
fout.close()
