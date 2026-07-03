# .venv\Lib\site-packages\hexdoc_hexcasting\book\recipes.py
# .venv\Lib\site-packages\hexdoc_hexcasting\_templates\recipes\hexcasting\brainsweep.html.jinja

from hexdoc.patchouli.page import EmptyPage
from .recipe import VoidsweepRecipe
from hexdoc_hexcasting.book.page.pages import BrainsweepPage

class VoidsweepRecipePage(BrainsweepPage, type="hexcasting:fishcasting/brainsweep_void"):
    pass
