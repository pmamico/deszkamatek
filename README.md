# Deszkamatek

## TODO

- [ ] jelmagyarázat
- [ ] nagyobb canvas
- [x] opcionálisan meg lehessen jeleníteni a nút, csap és vágás helyeket külön 
- [x] formon lehessen megadni a raktárat és a szoba méretét
- [ ] jelenjen meg a raktár tartalma kicsiben
- [x] látszódjanak a méretek a felületen
- [ ] hibát lehessen vinni a kalkulációba
- [ ] hiány esetén adja meg, hány plusz darabra lenne szükség
- [ ] kisebb deszkákkal logika, ha kettőből nem kirakható

## Docker

### Build the Docker image

```bash
docker build -t deszkamatek .
```

### Run the Docker container

```bash
docker run -p 8080:8080 deszkamatek
```

The application will be available at http://localhost:8080
