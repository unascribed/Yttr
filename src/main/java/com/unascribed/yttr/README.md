# Yttr Source Code
Hi! What're you looking for?

Some of it's obvious. Here's what's not:

## Blocks, potion effects, enchantments, entities, fluids, items

These are all under the `content` package for IDE organization. I got annoyed by working on a block
and an item at the same time and having to constantly scroll past the client package and lots of
other things.

## Block entities

These are side-by-side with their companion Block classes under `content.block.*`.

## Entity and block entity renderers, plus entity models

These are all in `client.render`.

## Other client renderers

Generally in the root `client` package. Some shared stuff is under `util`, and stuff related to
suit rendering and the suit font is under, well, `suit`.

## GUIs

`client.screen`

## Client initializer

`client.YttrClient`

## General initializers

The `Yttr` class is mostly utility methods. Actual content definition is in the `init` package,
organized by what kind of thing is being initialized. Members are generally sorted chronologically.

## Datagens

I don't use them.

## Assorted logic for mechanics

`mechanics` is single-purpose utility classes and interfaces used for specific mechanics in Yttr.
`util` is multi-purpose utility classes and such. Stuff that's specific to mixins is in `mixinsupport`.

## Data storage and structure generation

`world`.

## REI and other mods support

`compat`. At the time of writing, the only other mod Yttr integrates with is REI, so this package is
dedicated to that. The REI support classes may move to `compat.rei` in the future.
