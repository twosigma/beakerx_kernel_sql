@echo off
(
  REM Uninstall BeakerX notebook extension
  "%PREFIX%\Scripts\beakerx-kernel-sql.exe" "install"
) >>"%PREFIX%\.messages.txt" 2>&1