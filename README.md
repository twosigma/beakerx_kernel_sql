# BeakerX: Beaker kernel sql for Jupyter  

### Build and Install (linux and mac)

```
cd ./sql-dist
conda env create -n beakerx -f configuration.yml
conda activate beakerx # For conda versions prior to 4.6, run: source activate beakerx
(pip install -r requirements.txt --verbose)
beakerx_kernel_sql install
cd ..
```
