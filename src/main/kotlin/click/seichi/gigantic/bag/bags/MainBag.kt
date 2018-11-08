package click.seichi.gigantic.bag.bags

import click.seichi.gigantic.bag.Bag
import click.seichi.gigantic.button.buttons.BagButtons

/**
 * @author tar0ss
 */
object MainBag : Bag() {
    init {
        registerButton(9, BagButtons.PROFILE)
        registerButton(25, BagButtons.AFK)
        registerButton(32, BagButtons.SPECIAL_THANKS)
    }

}