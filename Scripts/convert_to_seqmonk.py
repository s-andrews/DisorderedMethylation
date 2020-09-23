#!python3
import sys

def main(infile) :

    meth_file = infile + "_meth.txt"
    disorder_file = infile + "_disorder.txt"

    with open(meth_file,"w") as out_meth:
            with open(disorder_file,"w") as out_disorder:
                with open(infile) as infh:
                    for line in infh:
                        sections = line.split("\t")
                        if sections[0] == "chr":
                            continue
                        for _ in range(int(sections[2])):
                            out_meth.write("\t".join([sections[0],sections[1],"+"]))
                            out_meth.write("\n")
                        for _ in range(int(sections[3])):
                            out_meth.write("\t".join([sections[0],sections[1],"-"]))
                            out_meth.write("\n")
                        for _ in range(int(sections[4])):
                            out_disorder.write("\t".join([sections[0],sections[1],"+"]))
                            out_disorder.write("\n")
                        for _ in range(int(sections[5])):
                            out_disorder.write("\t".join([sections[0],sections[1],"-"]))
                            out_disorder.write("\n")


if __name__ == "__main__":
    main(sys.argv[1])