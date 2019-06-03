FROM buildenv

WORKDIR /root
ADD tools/vcx-client-auto-respond.py .
ADD tools/team1.txn .

CMD ["python3.6", "vcx-client-auto-respond.py"]

