function Install-Chocolatey {
    <# 
    .SYNOPSIS 
        Insure Chocolatey is installed 
    .DESCRIPTION 
        Check if Chocolatey is installed. If not, then install it. 
    .EXAMPLE 
        Install-Chocolatey 
    #>
        [CmdletBinding(SupportsShouldProcess = $True)]
        param ()
        Write-Host "verifying chocolatey is installed"
        if (!(Test-Path "$($env:ProgramData)\chocolatey\choco.exe")) {
            Write-Host "installing chocolatey..."
            try {
                Set-ExecutionPolicy Bypass -Scope Process -Force; 
                [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; 
                iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
            }
            catch {
                Write-Host -Message $_.Exception.Message
            }
        }
        else {
            Write-Host "chocolatey is already installed"
        }
}

if (!(Test-Path "C:\temp")) {
  mkdir "C:\temp"
}
cd "C:\temp"

######################################################################
# install chocolatey, python3, and winrm
######################################################################
Install-Chocolatey

$output = (choco list python3 | select-string "0 packages found")
if($output -match "0 packages found") {
  choco install -y python3
}

######################################################################
# upgrade powershell to 5.X if needed
######################################################################
if ($PSVersionTable.PSVersion.Major -lt 5) {
  Write-Output "Upgrade PowerShell to 5.X"
  [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
  C:\ProgramData\chocolatey\choco upgrade powershell -y
  Restart-Computer -Force
}