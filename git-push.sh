git status
git add -A
git commit -m "commit"
git -c core.sshCommand="ssh -i /c/Users/aashraf/.ssh/id_rsa -o StrictHostKeyChecking=no -o GlobalKnownHostsFile=/dev/null -o UserKnownHostsFile=/dev/null" push

