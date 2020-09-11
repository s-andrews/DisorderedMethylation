# DisorderedMethylation
Code to calculate the level of disorder in bisulphite methylation patterns


## Usage

```java -jar DisorderedMethylation.jar bismark.bam outfile.txt```

## Output
```
chr     pos      meth_count unmeth_count  concordant  mixed
22      10526249 5          0             3           2
22      10526256 4          1             3           2
etc.
```
