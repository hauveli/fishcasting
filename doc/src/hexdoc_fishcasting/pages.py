# .venv\Lib\site-packages\hexdoc_hexcasting\book\recipes.py
# .venv\Lib\site-packages\hexdoc_hexcasting\_templates\recipes\hexcasting\brainsweep.html.jinja

from hexdoc.patchouli.page import EmptyPage
from hexdoc_hexcasting.book.page.pages import BrainsweepPage
from hexdoc_fishcasting.recipes import LightningRecipe

class VoidsweepPage(BrainsweepPage, type="hexcasting:fishcasting/brainsweep_void"):
    pass

# I dont know why this works, my intuition would say it shouldn't but I don't understand python well enough so this is fine
class VoidsweepPage(BrainsweepPage, type="hexcasting:fishcasting/brainsweep_void_clueless"):
    pass

# todo: implement this thing here then reference it from the others
class LightningPage(PageWithText, type="hexcasting:fishcasting/struck_by_lightning"):
    recipe: LightningRecipe


class LightningAltPage(LightningPage, type="hexcasting:fishcasting/struck_by_lightning_hint"):
    pass


class LightningAltPage(LightningPage, type="hexcasting:fishcasting/struck_by_lightning_clueless"):
    pass

