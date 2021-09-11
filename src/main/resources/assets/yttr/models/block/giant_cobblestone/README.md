This is sort of an exercise in doing large blocks quickly. Here's the secret (fish syntax):

`for x in (seq 0 3); for y in (seq 0 3); for z in (seq 0 3); jq '.elements = (.elements[]|select(.name == "'$x$y$z'"))' > $x$y$z.json < all.json; end; end; end`
